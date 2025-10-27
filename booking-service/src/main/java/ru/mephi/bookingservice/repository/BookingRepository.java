package ru.mephi.bookingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.bookingservice.model.Booking;
import ru.mephi.bookingservice.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByUser(User user, Pageable pageable);

    Optional<Booking> findBookingByIdAndUser(Long id, User user);

    Optional<Booking> findByRequestId(UUID requestId);
}
