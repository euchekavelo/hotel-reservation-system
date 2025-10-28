package ru.mephi.hotelmanagementservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final RoomReservationService roomReservationService;
    private final RoomReservationMapper roomReservationMapper;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    public ResponseEntity<RoomResponseDto> addRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Room room = roomMapper.roomRequestDtoToRoom(roomRequestDto);
        Room savedRoom = roomService.addRoom(roomRequestDto.getHotelId(), room);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomMapper.roomToRoomResponseDto(savedRoom));
    }

    @SecurityRequirements
    @PostMapping("/{roomId}/confirm-availability")
    public ResponseEntity<RoomReservationResponseDto> confirmRoomAvailability(@PathVariable long roomId,
                                             @RequestBody RoomReservationRequestDto roomReservationRequestDto) {

        RoomReservation roomReservation
                = roomReservationMapper.roomReservationRequestDtoToRoomReservation(roomReservationRequestDto);
        RoomReservation savedRoomReservation = roomReservationService.confirmRoomAvailability(roomReservation, roomId);

        return ResponseEntity.ok(roomReservationMapper.roomReservationToRoomReservationResponseDto(savedRoomReservation));
    }

    @SecurityRequirements
    @PostMapping("/{roomId}/release")
    public ResponseEntity<Void> removeRoomReservation(@PathVariable long roomId,
                                    @RequestBody ShortRoomReservationRequestDto shortRoomReservationRequestDto) {

        roomReservationService.removeRoomReservation(roomId, shortRoomReservationRequestDto.getReservationId());

        return ResponseEntity.noContent().build();
    }

    @SecurityRequirements
    @GetMapping("/recommend")
    public ResponseEntity<List<RoomResponseDto>> getListOfRecommendedRooms(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam String startDate,
                                                                           @RequestParam String endDate) {

        List<Room> recommendedRooms = roomService.getListOfRecommendedRooms(startDate, endDate, page, size);
        List<RoomResponseDto> roomResponseDtoList = recommendedRooms.stream()
                .map(roomMapper::roomToRoomResponseDto)
                .toList();

        return ResponseEntity.ok(roomResponseDtoList);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getListOfRooms(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam String startDate,
                                                                @RequestParam String endDate) {

        List<Room> rooms = roomService.getListOfRooms(startDate, endDate, page, size);
        List<RoomResponseDto> roomResponseDtoList = rooms.stream()
                .map(roomMapper::roomToRoomResponseDto)
                .toList();

        return ResponseEntity.ok(roomResponseDtoList);
    }
}
