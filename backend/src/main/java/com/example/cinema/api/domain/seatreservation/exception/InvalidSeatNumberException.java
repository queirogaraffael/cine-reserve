package com.example.cinema.api.domain.seatreservation.exception;

public class InvalidSeatNumberException extends RuntimeException {
    public InvalidSeatNumberException(String message) {
        super(message);
    }
}
