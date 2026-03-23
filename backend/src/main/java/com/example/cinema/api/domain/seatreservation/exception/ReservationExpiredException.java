package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class ReservationExpiredException extends StateConflictException {
    public ReservationExpiredException(String message) {
        super(message);
    }
}
