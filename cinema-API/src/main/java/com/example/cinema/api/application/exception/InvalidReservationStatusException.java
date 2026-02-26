package com.example.cinema.api.application.exception;

public class InvalidReservationStatusException extends RuntimeException {
    public InvalidReservationStatusException(String message) {
        super(message);
    }
}
