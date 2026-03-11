package com.example.cinema.api.domain.ticket.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class SeatAlreadyReservedException extends ConflictException {
    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
