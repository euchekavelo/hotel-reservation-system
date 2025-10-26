package ru.mephi.hotelmanagementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.hotelmanagementservice.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
