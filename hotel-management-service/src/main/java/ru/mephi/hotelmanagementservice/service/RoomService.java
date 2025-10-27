package ru.mephi.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.mephi.hotelmanagementservice.dto.response.RoomResponseDto;
import ru.mephi.hotelmanagementservice.exception.RoomNotFoundException;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelService hotelService;

    public Room addRoom(long hotelId, Room room) {
        Hotel findedHotel = hotelService.getHotelById(hotelId);
        room.setHotel(findedHotel);
        room.setTimesBooked(0L);

        return roomRepository.save(room);
    }

    public Room getRoomByIdWithPessimisticWriteLock(long roomId) {
        return roomRepository.findRoomByIdWithPessimisticWriteLock(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Номер с указанным идентификатором не найден."));
    }

    public List<Room> getListOfRecommendedRooms(String startDate, String endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "times_booked");

        return roomRepository.getListOfRecommendedRooms(startDate, endDate, pageable);
    }
}
