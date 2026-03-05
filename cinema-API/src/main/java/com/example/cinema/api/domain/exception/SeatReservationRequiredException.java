package com.example.cinema.api.domain.exception;

public class SeatReservationRequiredException extends RuntimeException {
    public SeatReservationRequiredException(String message) {
        super(message);
    }
}
