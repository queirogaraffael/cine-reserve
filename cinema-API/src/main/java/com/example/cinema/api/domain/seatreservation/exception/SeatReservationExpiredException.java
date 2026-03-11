package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.shared.exception.BadRequestException;
import com.example.cinema.api.shared.exception.ConflictException;

public class SeatReservationExpiredException extends ConflictException {
    public SeatReservationExpiredException(String message) {
        super(message);
    }
}
