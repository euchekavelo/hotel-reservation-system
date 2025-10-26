package ru.mephi.bookingservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    private long roomId;
    private Boolean autoSelect;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
