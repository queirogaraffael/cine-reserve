package com.example.cinema.api.domain.seatreservation.exception;

public class ReservationCannotBeCancelledException extends RuntimeException {
    public ReservationCannotBeCancelledException(String message) {
        super(message);
    }
}
