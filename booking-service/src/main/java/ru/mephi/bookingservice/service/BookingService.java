package ru.mephi.bookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.bookingservice.adapter.api.HotelManagementServiceApiAdapter;
import ru.mephi.bookingservice.exception.BookingNotFoundException;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.model.enums.Status;
import ru.mephi.bookingservice.repository.BookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final HotelManagementServiceApiAdapter hotelManagementServiceApiAdapter;

    @Transactional
    public Booking createBooking(Booking booking, long userId, Boolean autoSelect, UUID requestId) {
        Booking findedBooking = bookingRepository.findByRequestId(requestId).orElse(null);
        if (findedBooking != null) {
            return findedBooking;
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate = booking.getStartDate();
        LocalDate endDate = booking.getEndDate();
        if (startDate.isAfter(endDate) || (!startDate.isAfter(now) && !endDate.isAfter(now))) {
            throw new IllegalArgumentException("Дата начала заезда не должна превышать дату окончания заезда, " +
                    "а также даты должны быть больше текущей даты.");
        }

        User currentUser = userService.getUserById(userId);

        if (autoSelect) {
            long roomId = hotelManagementServiceApiAdapter.getRecommendedRoomByDate(startDate, endDate);
            booking.setRoomId(roomId);
        }

        booking.setRequestId(requestId);
        booking.setUser(currentUser);
        booking.setStatus(Status.PENDING);
        Booking savedBooking = bookingRepository.saveAndFlush(booking);
        log.info("[Запрос ID {}] Сформировано бронирование с ID {} в статусе {}", requestId,
                savedBooking.getId(), savedBooking.getStatus().name());

        long reservationId = 0L;
        try {
            reservationId = hotelManagementServiceApiAdapter
                    .confirmRoomAvailability(savedBooking.getRoomId(), savedBooking.getId(), startDate, endDate, requestId);
            savedBooking.setStatus(Status.CONFIRMED);
        } catch (Exception ex) {
            log.error("[Запрос ID {}] При формировании бронирования с ID {} возникла ошибка: {}", requestId,
                    savedBooking.getId(), ex.getMessage());
            savedBooking.setStatus(Status.CANCELLED);
            performCompensationForBooking(requestId, reservationId, savedBooking.getId(), booking.getRoomId());
        }
        log.info("[Запрос ID {}] Для бронирования с ID {} изменен статус на {}", requestId,
                savedBooking.getId(), savedBooking.getStatus().name());

        return bookingRepository.saveAndFlush(savedBooking);
    }

    public List<Booking> findAllForUserWithPageable(long userId, int page, int size) {
        User currentUser = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookings = bookingRepository.findAllByUser(currentUser, pageable);

        return bookings.getContent();
    }

    public Booking getBookingByIdAndUserId(long bookingId, long userId) {
        User currentUser = userService.getUserById(userId);

        return bookingRepository.findBookingByIdAndUser(bookingId, currentUser)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с указанным идентификатором " +
                        "для пользователя не найдено."));
    }

    @Transactional
    public void deleteBookingByIdAndUserId(long bookingId, long userId) {
        Booking booking = getBookingByIdAndUserId(bookingId, userId);
        bookingRepository.delete(booking);

        hotelManagementServiceApiAdapter.deleteRoomReservationByBookingId(bookingId);
    }

    private void performCompensationForBooking(UUID requestId,long reservationId, long bookingId, long roomId) {
        if (reservationId != 0L) {
            try {
                hotelManagementServiceApiAdapter.removeRoomReservation(roomId, reservationId);
            } catch (Exception exception) {
                log.error("[Запрос ID {}] При попытке выполнить отмену резерва номера для бронирования с ID {} " +
                        "возникла ошибка: {}", requestId, bookingId, exception.getMessage());
            }
        }
    }
}
