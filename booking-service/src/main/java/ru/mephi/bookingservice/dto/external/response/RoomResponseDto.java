package ru.mephi.bookingservice.dto.external.response;

import lombok.Data;

@Data
public class RoomResponseDto {

    private long id;
    private String number;
    private long hotelId;
}
