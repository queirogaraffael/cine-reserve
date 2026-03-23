package com.example.cinema.api.domain.seatreservation.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
