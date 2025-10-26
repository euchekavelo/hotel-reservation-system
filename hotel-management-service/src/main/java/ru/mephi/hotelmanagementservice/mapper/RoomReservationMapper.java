package ru.mephi.hotelmanagementservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mephi.hotelmanagementservice.dto.request.RoomReservationRequestDto;
import ru.mephi.hotelmanagementservice.dto.response.RoomReservationResponseDto;
import ru.mephi.hotelmanagementservice.model.RoomReservation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomReservationMapper {

    RoomReservation roomReservationRequestDtoToRoomReservation(RoomReservationRequestDto roomReservationRequestDto);

    RoomReservationResponseDto roomReservationToRoomReservationResponseDto(RoomReservation roomReservation);
}
