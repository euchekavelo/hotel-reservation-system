package ru.mephi.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.hotelmanagementservice.exception.RoomReservationNotFoundException;
import ru.mephi.hotelmanagementservice.exception.RoomReservationOverlappingException;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.model.RoomReservation;
import ru.mephi.hotelmanagementservice.repository.RoomReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomReservationService {

    private final RoomReservationRepository roomReservationRepository;
    private final RoomService roomService;

    @Transactional
    public RoomReservation confirmRoomAvailability(RoomReservation roomReservation, long roomId) {
        Room findedRoom = roomService.getRoomByIdWithPessimisticWriteLock(roomId);
        List<RoomReservation> roomReservationList = roomReservationRepository
                .findOverlappingRoomReservation(findedRoom, roomReservation.getStartDate(), roomReservation.getEndDate());

        if (!roomReservationList.isEmpty()) {
            throw new RoomReservationOverlappingException("При попытке резервации номера указаны перекрывающиеся " +
                    "даты с уже имеющимися резервами для данного номера.");
        }

        findedRoom.setTimesBooked(findedRoom.getTimesBooked() + 1);
        roomReservation.setRoom(findedRoom);

        return roomReservationRepository.save(roomReservation);
    }

    @Transactional
    public void removeRoomReservation(long roomId, long reservationId) {
        Room findedRoom = roomService.getRoomByIdWithPessimisticWriteLock(roomId);
        RoomReservation roomReservation = roomReservationRepository
                .findById(reservationId).orElseThrow(() ->
                        new RoomReservationNotFoundException("Резерв номера с указанным идентификатором не найден."));

        roomReservationRepository.delete(roomReservation);
        findedRoom.setTimesBooked(findedRoom.getTimesBooked() - 1);
    }
}
