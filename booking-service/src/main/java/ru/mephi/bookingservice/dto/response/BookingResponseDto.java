package ru.mephi.bookingservice.dto.response;

import lombok.Data;
import ru.mephi.bookingservice.model.enums.Status;

@Data
public class BookingResponseDto {

    private long id;
    private Status status;
}
