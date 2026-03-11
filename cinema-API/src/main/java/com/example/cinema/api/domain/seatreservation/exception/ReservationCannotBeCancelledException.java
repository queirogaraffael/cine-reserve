package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class ReservationCannotBeCancelledException extends ConflictException {
    public ReservationCannotBeCancelledException(String message) {
        super(message);
    }
}
