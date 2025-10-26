package ru.mephi.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mephi.hotelmanagementservice.exception.RoomNotFoundException;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.repository.RoomRepository;

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
}
