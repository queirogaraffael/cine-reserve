package com.example.cinema.api.domain.seatreservation.exception;

public class SeatReservationExpiredException extends RuntimeException {
    public SeatReservationExpiredException(String message) {
        super(message);
    }
}
