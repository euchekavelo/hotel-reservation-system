package ru.mephi.hotelmanagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.hotelmanagementservice.dto.request.RoomRequestDto;
import ru.mephi.hotelmanagementservice.dto.request.RoomReservationRequestDto;
import ru.mephi.hotelmanagementservice.dto.request.ShortRoomReservationRequestDto;
import ru.mephi.hotelmanagementservice.dto.response.RoomReservationResponseDto;
import ru.mephi.hotelmanagementservice.dto.response.RoomResponseDto;
import ru.mephi.hotelmanagementservice.mapper.RoomMapper;
import ru.mephi.hotelmanagementservice.mapper.RoomReservationMapper;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.model.RoomReservation;
import ru.mephi.hotelmanagementservice.service.RoomReservationService;
import ru.mephi.hotelmanagementservice.service.RoomService;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final RoomReservationService roomReservationService;
    private final RoomReservationMapper roomReservationMapper;

    @PostMapping
    public ResponseEntity<RoomResponseDto> addRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Room room = roomMapper.roomRequestDtoToRoom(roomRequestDto);
        Room savedRoom = roomService.addRoom(roomRequestDto.getHotelId(), room);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomMapper.roomToRoomResponseDto(savedRoom));
    }

    @PostMapping("/{roomId}/confirm-availability")
    public ResponseEntity<RoomReservationResponseDto> confirmRoomAvailability(@PathVariable long roomId,
                                             @RequestBody RoomReservationRequestDto roomReservationRequestDto) {

        RoomReservation roomReservation
                = roomReservationMapper.roomReservationRequestDtoToRoomReservation(roomReservationRequestDto);
        RoomReservation savedRoomReservation = roomReservationService.confirmRoomAvailability(roomReservation, roomId);

        return ResponseEntity.ok(roomReservationMapper.roomReservationToRoomReservationResponseDto(savedRoomReservation));
    }

    @PostMapping("/{roomId}/release")
    public ResponseEntity<Void> removeRoomReservation(@PathVariable long roomId,
                                    @RequestBody ShortRoomReservationRequestDto shortRoomReservationRequestDto) {

        roomReservationService.removeRoomReservation(roomId, shortRoomReservationRequestDto.getReservationId());

        return ResponseEntity.noContent().build();
    }
}
