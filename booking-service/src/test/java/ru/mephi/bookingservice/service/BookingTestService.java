package ru.mephi.bookingservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.mephi.bookingservice.adapter.api.HotelManagementServiceApiAdapter;
import ru.mephi.bookingservice.exception.ExternalServiceException;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.model.enums.Role;
import ru.mephi.bookingservice.model.enums.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class BookingTestService {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @MockitoBean
    private HotelManagementServiceApiAdapter hotelManagementServiceApiAdapter;

    private static User userWithUserRole;

    @BeforeAll
    static void beforeAll() {
        userWithUserRole = new User();
        userWithUserRole.setRole(Role.USER);
        userWithUserRole.setUsername("test2");
        userWithUserRole.setPassword("test2");
        userWithUserRole.setBookingList(new ArrayList<>());
    }

    @Test
    void createBookingSuccess() {
        Mockito.when(hotelManagementServiceApiAdapter.confirmRoomAvailability(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(UUID.class))).thenReturn(1L);

        User savedUser = userService.createUser(userWithUserRole);
        Booking booking = new Booking();
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(2));
        booking.setRoomId(10L);

        Booking savedBooking = bookingService.createBooking(booking, savedUser.getId(),
                false, UUID.randomUUID().toString());

        Assertions.assertNotNull(savedBooking.getId());
        Assertions.assertEquals(Status.CONFIRMED, savedBooking.getStatus());
    }

    @Test
    void createBookingFailure() {
        User savedUser = userService.createUser(userWithUserRole);
        Booking booking = new Booking();
        booking.setStatus(Status.PENDING);
        booking.setStartDate(LocalDate.now().plusDays(2));
        booking.setEndDate(LocalDate.now().plusDays(1));
        booking.setRoomId(11L);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(booking, savedUser.getId(), false,
                        UUID.randomUUID().toString()));
    }

    @Test
    void createBookingTestWithCompensation() {
        Mockito.when(hotelManagementServiceApiAdapter.confirmRoomAvailability(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(ExternalServiceException.class);

        User savedUser = userService.createUser(userWithUserRole);
        Booking booking = new Booking();
        booking.setStatus(Status.PENDING);
        booking.setStartDate(LocalDate.now().plusDays(10));
        booking.setEndDate(LocalDate.now().plusDays(12));
        booking.setRoomId(12L);

        Booking savedBooking = bookingService.createBooking(booking, savedUser.getId(),
                false, UUID.randomUUID().toString());

        Assertions.assertEquals(Status.CANCELLED, savedBooking.getStatus());
    }
}
