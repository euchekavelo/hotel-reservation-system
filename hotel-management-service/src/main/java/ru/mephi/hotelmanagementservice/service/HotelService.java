package ru.mephi.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mephi.hotelmanagementservice.exception.HotelNotFoundException;
import ru.mephi.hotelmanagementservice.model.Hotel;
import ru.mephi.hotelmanagementservice.repository.HotelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public Hotel addHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel getHotelById(long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Отель с указанным идентификатором не найден"));
    }

    public List<Hotel> findAllWithPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findAll(pageable);

        return hotels.getContent();
    }
}
