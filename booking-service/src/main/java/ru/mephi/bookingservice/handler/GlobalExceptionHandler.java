package ru.mephi.bookingservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mephi.bookingservice.dto.response.ErrorResponseDto;
import ru.mephi.bookingservice.exception.BookingNotFoundException;
import ru.mephi.bookingservice.exception.ExternalServiceException;
import ru.mephi.bookingservice.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, BookingNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({ExternalServiceException.class})
    public ResponseEntity<ErrorResponseDto> handleInternalServerException(Exception ex) {
        return ResponseEntity.internalServerError().body(getErrorResponseDto(ex.getMessage()));
    }

    private ErrorResponseDto getErrorResponseDto(String message) {
        return ErrorResponseDto.builder()
                .message(message)
                .result(false)
                .build();
    }
}
