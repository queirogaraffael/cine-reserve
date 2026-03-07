package com.example.cinema.api.domain.exception;

public class SeatNumberRequiredException extends RuntimeException {
    public SeatNumberRequiredException(String message) {
        super(message);
    }
}
