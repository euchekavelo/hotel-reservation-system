package ru.mephi.hotelmanagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.hotelmanagementservice.dto.request.HotelRequestDto;
import ru.mephi.hotelmanagementservice.dto.response.HotelResponseDto;
import ru.mephi.hotelmanagementservice.mapper.HotelMapper;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final HotelMapper hotelMapper;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    public ResponseEntity<HotelResponseDto> addHotel(@RequestBody HotelRequestDto hotelRequestDto) {
        Hotel hotel = hotelMapper.hotelRequestDtoToHotel(hotelRequestDto);
        Hotel savedHotel = hotelService.addHotel(hotel);

        return ResponseEntity.status(HttpStatus.CREATED).body(hotelMapper.hotelToHotelResponseDto(savedHotel));
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping
    public ResponseEntity<List<HotelResponseDto>> getAllHotels(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {

        List<Hotel> hotels = hotelService.findAllWithPageable(page, size);

        return ResponseEntity.ok(hotelMapper.hotelsToHotelListResponseDto(hotels));
    }
}
