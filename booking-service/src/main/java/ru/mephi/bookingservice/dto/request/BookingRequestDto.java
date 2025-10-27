package ru.mephi.bookingservice.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    private long roomId;
    private Boolean autoSelect;
    private LocalDate startDate;
    private LocalDate endDate;
}
