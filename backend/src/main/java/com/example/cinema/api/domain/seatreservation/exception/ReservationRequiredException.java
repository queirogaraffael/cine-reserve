package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class ReservationRequiredException extends ValidationException {
    public ReservationRequiredException(String message) {
        super(message);
    }
}
