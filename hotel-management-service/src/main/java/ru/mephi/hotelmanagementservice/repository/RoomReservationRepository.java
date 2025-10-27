package ru.mephi.hotelmanagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mephi.hotelmanagementservice.model.Room;
import ru.mephi.hotelmanagementservice.model.RoomReservation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {

    @Query("SELECT rr FROM RoomReservation rr WHERE rr.room = :room AND " +
            "(rr.startDate <= :endDate AND rr.endDate >= :startDate)")
    List<RoomReservation> findOverlappingRoomReservation(Room room, LocalDate startDate, LocalDate endDate);

    long deleteByBookingId(long bookingId);
}
