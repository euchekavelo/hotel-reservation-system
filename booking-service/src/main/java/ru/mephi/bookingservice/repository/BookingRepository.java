package ru.mephi.bookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.bookingservice.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
