package ru.mephi.hotelmanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.hotelmanagementservice.dto.request.RoomReservationRequestDto;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.repository.HotelRepository;
import ru.mephi.hotelmanagementservice.repository.RoomRepository;
import ru.mephi.hotelmanagementservice.repository.RoomReservationRepository;
import ru.mephi.hotelmanagementservice.service.HotelService;
import ru.mephi.hotelmanagementservice.service.RoomService;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoomTestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    private static Hotel hotel;
    private static Room room;

    @BeforeAll
    static void beforeAll() {
        hotel = new Hotel();
        hotel.setName("Hotel 1");
        hotel.setAddress("Hotel Address");

        room = new Room();
        room.setNumber("test_room");
    }

    @AfterEach
    void tearDown() {
        roomReservationRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    @Test
    void confirmRoomAvailabilitySuccess() throws Exception {
        Hotel savedHotel = hotelService.addHotel(hotel);
        Room savedRoom = roomService.addRoom(savedHotel.getId(), room);

        RoomReservationRequestDto roomReservationRequestDto = new RoomReservationRequestDto();
        roomReservationRequestDto.setRequestId(UUID.randomUUID());
        roomReservationRequestDto.setStartDate(LocalDate.now().plusMonths(1));
        roomReservationRequestDto.setEndDate(LocalDate.now().plusMonths(2));
        roomReservationRequestDto.setBookingId(1L);
        Long roomId = savedRoom.getId();

        mockMvc.perform(post("/rooms/{roomId}/confirm-availability", roomId)
                        .content(objectMapper.writeValueAsString(roomReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("id").isNotEmpty());
    }

    @Test
    void confirmRoomAvailabilityFailure() throws Exception {
        RoomReservationRequestDto roomReservationRequestDto = new RoomReservationRequestDto();
        roomReservationRequestDto.setRequestId(UUID.randomUUID());
        roomReservationRequestDto.setStartDate(LocalDate.now().plusMonths(3));
        roomReservationRequestDto.setEndDate(LocalDate.now().plusMonths(4));
        roomReservationRequestDto.setBookingId(1L);

        mockMvc.perform(post("/rooms/{roomId}/confirm-availability", 1000)
                        .content(objectMapper.writeValueAsString(roomReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
