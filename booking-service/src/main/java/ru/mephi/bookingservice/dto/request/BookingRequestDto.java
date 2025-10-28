package ru.mephi.bookingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    private long roomId;
    private Boolean autoSelect;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;
}
