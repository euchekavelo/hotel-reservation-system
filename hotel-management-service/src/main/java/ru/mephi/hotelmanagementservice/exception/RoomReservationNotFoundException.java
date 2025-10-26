package ru.mephi.hotelmanagementservice.exception;

public class RoomReservationNotFoundException extends RuntimeException {

    public RoomReservationNotFoundException(String message) {
        super(message);
    }
}
