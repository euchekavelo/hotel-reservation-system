package ru.mephi.hotelmanagementservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mephi.hotelmanagementservice.model.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :roomId")
    Optional<Room> findRoomByIdWithPessimisticWriteLock(long roomId);

    @Query(value =
            "SELECT rs.* \n" +
            "FROM   rooms rs \n" +
            "WHERE  rs.id NOT IN (\n " +
                        "SELECT rrs.room_id \n" +
                        "FROM   room_reservations rrs \n" +
                        "WHERE  rrs.start_date <= :endDate AND rrs.end_date >= :startDate \n" +
                    ")", nativeQuery = true)
    List<Room> getListOfAvailableRooms(String startDate, String endDate, Pageable pageable);
}
