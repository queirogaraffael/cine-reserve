package com.example.cinema.api.application.exception;

public class InvalidSeatNumberException extends RuntimeException {
    public InvalidSeatNumberException(String message) {
        super(message);
    }
}
