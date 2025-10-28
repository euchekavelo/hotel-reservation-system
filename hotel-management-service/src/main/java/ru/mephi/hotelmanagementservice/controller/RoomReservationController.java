package ru.mephi.hotelmanagementservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.hotelmanagementservice.service.RoomReservationService;

@RestController
@RequestMapping("/room-reservations")
@RequiredArgsConstructor
public class RoomReservationController {

    private final RoomReservationService roomReservationService;

    @SecurityRequirements
    @DeleteMapping("/by-booking/{bookingId}")
    public ResponseEntity<Void> deleteRoomReservationById(@PathVariable long bookingId) {
        roomReservationService.deleteRoomReservationByBookingId(bookingId);

        return ResponseEntity.noContent().build();
    }
}
