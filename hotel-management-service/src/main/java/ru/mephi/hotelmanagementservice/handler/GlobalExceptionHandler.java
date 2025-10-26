package ru.mephi.hotelmanagementservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mephi.hotelmanagementservice.dto.response.ErrorResponseDto;
import ru.mephi.hotelmanagementservice.exception.HotelNotFoundException;
import ru.mephi.hotelmanagementservice.exception.RoomNotFoundException;
import ru.mephi.hotelmanagementservice.exception.RoomReservationNotFoundException;
import ru.mephi.hotelmanagementservice.exception.RoomReservationOverlappingException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HotelNotFoundException.class, RoomNotFoundException.class, RoomReservationNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, RoomReservationOverlappingException.class})
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(ex.getMessage()));
    }

    private ErrorResponseDto getErrorResponseDto(String message) {
        return ErrorResponseDto.builder()
                .message(message)
                .result(false)
                .build();
    }
}
