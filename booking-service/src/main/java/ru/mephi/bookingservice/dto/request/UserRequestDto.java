package ru.mephi.bookingservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDto {

    private String username;

    private String password;

    @Pattern(regexp = "USER|ADMIN")
    private String role;
}
