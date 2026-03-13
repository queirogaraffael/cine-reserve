package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class SeatReservationRequiredException extends ValidationException {
    public SeatReservationRequiredException(String message) {
        super(message);
    }
}
