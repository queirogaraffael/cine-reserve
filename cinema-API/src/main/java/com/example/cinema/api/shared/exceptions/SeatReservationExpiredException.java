package com.example.cinema.api.shared.exceptions;

public class SeatReservationExpiredException extends RuntimeException {
    public SeatReservationExpiredException(String message) {
        super(message);
    }
}
