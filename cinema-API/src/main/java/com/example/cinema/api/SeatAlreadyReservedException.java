package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class SeatAlreadyReservedException extends StateConflictException {
    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
