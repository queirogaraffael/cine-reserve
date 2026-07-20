package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class ReservationCannotBeCancelledException extends StateConflictException {
    public ReservationCannotBeCancelledException(String message) {
        super(message);
    }
}
