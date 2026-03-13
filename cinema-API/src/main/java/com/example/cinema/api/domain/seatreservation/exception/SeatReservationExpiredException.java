package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class SeatReservationExpiredException extends StateConflictException {
    public SeatReservationExpiredException(String message) {
        super(message);
    }
}
