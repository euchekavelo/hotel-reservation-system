package ru.mephi.bookingservice.dto.external.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomReservationRequestDto {

    private long bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID requestId;
}
