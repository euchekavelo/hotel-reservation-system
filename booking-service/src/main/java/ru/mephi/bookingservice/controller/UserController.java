package ru.mephi.bookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User savedUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserResponseDto(savedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable Long userId,
                                                          @Valid @RequestBody UserRequestDto userRequestDto) {

        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User updatedUser = userService.updateUserById(userId, user);

        return ResponseEntity.ok(userMapper.userToUserResponseDto(updatedUser));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User savedUser = userService.createUser(user);
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .token(tokenService.createToken(Long.toString(savedUser.getId()), savedUser.getRole().name()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponseDto);
    }

    @PostMapping("/auth")
    public ResponseEntity<TokenResponseDto> authenticateUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userService.getUserByUsernameAndPassword(userRequestDto.getUsername(), userRequestDto.getPassword());
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .token(tokenService.createToken(Long.toString(user.getId()), user.getRole().name()))
                .build();

        return ResponseEntity.ok(tokenResponseDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
