package ru.mephi.bookingservice.dto.external.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomReservationRequestDto {

    private long bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
}
