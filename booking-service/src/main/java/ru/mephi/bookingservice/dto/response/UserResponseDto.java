package ru.mephi.bookingservice.dto.response;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String role;
}
