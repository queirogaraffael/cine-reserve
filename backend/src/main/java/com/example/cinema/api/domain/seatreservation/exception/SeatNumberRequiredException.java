package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class SeatNumberRequiredException extends ValidationException {
    public SeatNumberRequiredException(String message) {
        super(message);
    }
}
