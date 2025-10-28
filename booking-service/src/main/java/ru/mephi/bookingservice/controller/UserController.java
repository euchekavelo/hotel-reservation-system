package ru.mephi.bookingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.bookingservice.dto.request.ShortUserRequestDto;
import ru.mephi.bookingservice.dto.request.UserRequestDto;
import ru.mephi.bookingservice.dto.response.TokenResponseDto;
import ru.mephi.bookingservice.dto.response.UserResponseDto;
import ru.mephi.bookingservice.mapper.UserMapper;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.service.TokenService;
import ru.mephi.bookingservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User savedUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserResponseDto(savedUser));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable Long userId,
                                                          @Valid @RequestBody UserRequestDto userRequestDto) {

        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User updatedUser = userService.updateUserById(userId, user);

        return ResponseEntity.ok(userMapper.userToUserResponseDto(updatedUser));
    }

    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User savedUser = userService.createUser(user);
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .token(tokenService.createToken(Long.toString(savedUser.getId()), savedUser.getRole().name()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponseDto);
    }

    @SecurityRequirements
    @PostMapping("/auth")
    public ResponseEntity<TokenResponseDto> authenticateUser(@Valid @RequestBody ShortUserRequestDto shortUserRequestDto) {
        User user = userService.getUserByUsernameAndPassword(shortUserRequestDto.getUsername(),
                shortUserRequestDto.getPassword());
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .token(tokenService.createToken(Long.toString(user.getId()), user.getRole().name()))
                .build();

        return ResponseEntity.ok(tokenResponseDto);
    }
}
