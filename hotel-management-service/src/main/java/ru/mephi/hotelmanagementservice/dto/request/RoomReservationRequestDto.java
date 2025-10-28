package ru.mephi.hotelmanagementservice.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomReservationRequestDto {

    private Long bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID requestId;
}
