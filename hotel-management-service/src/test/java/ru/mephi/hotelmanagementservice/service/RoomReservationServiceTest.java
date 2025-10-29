package ru.mephi.hotelmanagementservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mephi.hotelmanagementservice.exception.RoomNotFoundException;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.model.RoomReservation;
import ru.mephi.hotelmanagementservice.repository.HotelRepository;
import ru.mephi.hotelmanagementservice.repository.RoomRepository;
import ru.mephi.hotelmanagementservice.repository.RoomReservationRepository;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
public class RoomReservationServiceTest {

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

    @Autowired
    private RoomReservationService roomReservationService;

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
    void confirmRoomAvailabilitySuccess() {
        Hotel savedHotel = hotelService.addHotel(hotel);
        Room savedRoom = roomService.addRoom(savedHotel.getId(), room);

        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setStartDate(LocalDate.now().plusMonths(4));
        roomReservation.setEndDate(LocalDate.now().plusMonths(5));

        RoomReservation savedRoomReservation = roomReservationService
                .confirmRoomAvailability(roomReservation, savedRoom.getId());

        Assertions.assertNotNull(savedRoomReservation.getId());
    }

    @Test
    void confirmRoomAvailabilityFailure() {
        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setStartDate(LocalDate.now().plusMonths(4));
        roomReservation.setEndDate(LocalDate.now().plusMonths(5));

        Assertions.assertThrows(RoomNotFoundException.class,
                () -> roomReservationService.confirmRoomAvailability(roomReservation, 100));
    }
}
