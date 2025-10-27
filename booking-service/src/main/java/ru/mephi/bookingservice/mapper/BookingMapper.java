package ru.mephi.bookingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mephi.bookingservice.dto.request.BookingRequestDto;
import ru.mephi.bookingservice.dto.response.BookingResponseDto;
import ru.mephi.bookingservice.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    Booking bookingRequestDtoToBooking(BookingRequestDto bookingRequestDto);

    BookingResponseDto bookingToBookingResponseDto(Booking booking);

    List<BookingResponseDto> bookingsToBookingResponseDto(List<Booking> bookings);
}
