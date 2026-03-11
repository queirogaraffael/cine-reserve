package com.example.cinema.api.application.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class ReservationCannotBeConsumedException extends ConflictException {
    public ReservationCannotBeConsumedException(String message) {
        super(message);
    }
}
