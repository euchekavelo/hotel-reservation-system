package ru.mephi.bookingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.bookingservice.dto.request.BookingRequestDto;
import ru.mephi.bookingservice.dto.response.BookingResponseDto;
import ru.mephi.bookingservice.mapper.BookingMapper;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.service.BookingService;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public ResponseEntity<BookingResponseDto> createBooking(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody BookingRequestDto bookingRequestDto) {

        Booking booking = bookingMapper.bookingRequestDtoToBooking(bookingRequestDto);
        Booking savedBooking = bookingService.createBooking(booking, Long.parseLong(jwt.getSubject()),
                bookingRequestDto.getAutoSelect());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.bookingToBookingResponseDto(savedBooking));
    }
}
