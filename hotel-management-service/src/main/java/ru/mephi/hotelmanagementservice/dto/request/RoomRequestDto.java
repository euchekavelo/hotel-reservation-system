package ru.mephi.hotelmanagementservice.dto.request;

import lombok.Data;

@Data
public class RoomRequestDto {

    private long hotelId;
    private String number;
}
