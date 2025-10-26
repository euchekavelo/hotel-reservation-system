package ru.mephi.hotelmanagementservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mephi.hotelmanagementservice.dto.request.HotelRequestDto;
import ru.mephi.hotelmanagementservice.dto.response.HotelResponseDto;
import ru.mephi.hotelmanagementservice.model.Hotel;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HotelMapper {

    Hotel hotelRequestDtoToHotel(HotelRequestDto hotelRequestDto);

    HotelResponseDto hotelToHotelResponseDto(Hotel hotel);

    List<HotelResponseDto> hotelsToHotelListResponseDto(List<Hotel> hotels);
}
