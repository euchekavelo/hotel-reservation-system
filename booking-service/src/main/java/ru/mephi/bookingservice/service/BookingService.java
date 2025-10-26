package ru.mephi.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.model.enums.Status;
import ru.mephi.bookingservice.repository.BookingRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Transactional
    public Booking createBooking(Booking booking, long userId, Boolean autoSelect) {
        LocalDate startDate = booking.getStartDate();
        LocalDate endDate = booking.getEndDate();
        User currentUser = userService.getUserById(userId);
        booking.setUser(currentUser);
        booking.setStatus(Status.PENDING);

        Booking newBooking = bookingRepository.saveAndFlush(booking);



    }
}
