package ru.mephi.bookingservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.mephi.bookingservice.dto.request.BookingRequestDto;
import ru.mephi.bookingservice.dto.response.BookingResponseDto;
import ru.mephi.bookingservice.mapper.BookingMapper;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody BookingRequestDto bookingRequestDto,
                                                            HttpServletRequest request) {

        String requestId = request.getHeader("X-Request-Id");
        Booking booking = bookingMapper.bookingRequestDtoToBooking(bookingRequestDto);
        Booking savedBooking = bookingService.createBooking(booking, Long.parseLong(jwt.getSubject()),
                bookingRequestDto.getAutoSelect(), requestId);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.bookingToBookingResponseDto(savedBooking));
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsForUser(@AuthenticationPrincipal Jwt jwt,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {

        List<Booking> bookingList = bookingService.findAllForUserWithPageable(Long.parseLong(jwt.getSubject()), page, size);

        return ResponseEntity.ok(bookingMapper.bookingsToBookingResponseDto(bookingList));
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingByIdForUser(@AuthenticationPrincipal Jwt jwt,
                                                                    @PathVariable long bookingId) {

        Booking booking = bookingService.getBookingByIdAndUserId(bookingId, Long.parseLong(jwt.getSubject()));

        return ResponseEntity.ok(bookingMapper.bookingToBookingResponseDto(booking));
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBookingByIdForUser(@AuthenticationPrincipal Jwt jwt, @PathVariable long bookingId) {
        bookingService.deleteBookingByIdAndUserId(bookingId, Long.parseLong(jwt.getSubject()));

        return ResponseEntity.noContent().build();
    }
}
