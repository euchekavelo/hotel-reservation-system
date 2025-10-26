package ru.mephi.hotelmanagementservice.exception;

public class RoomReservationOverlappingException extends RuntimeException {

    public RoomReservationOverlappingException(String message) {
        super(message);
    }
}
