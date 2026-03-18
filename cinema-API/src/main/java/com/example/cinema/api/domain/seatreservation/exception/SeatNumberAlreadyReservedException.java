package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class SeatNumberAlreadyReservedException extends StateConflictException {
    public SeatNumberAlreadyReservedException(String message) {
        super(message);
    }
}
