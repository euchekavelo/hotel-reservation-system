package ru.mephi.hotelmanagementservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mephi.hotelmanagementservice.dto.request.RoomRequestDto;
import ru.mephi.hotelmanagementservice.dto.response.RoomResponseDto;
import ru.mephi.hotelmanagementservice.model.Room;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    Room roomRequestDtoToRoom(RoomRequestDto roomRequestDto);

    @Mapping(target = "hotelId", source = "room.hotel.id")
    RoomResponseDto roomToRoomResponseDto(Room room);
}
