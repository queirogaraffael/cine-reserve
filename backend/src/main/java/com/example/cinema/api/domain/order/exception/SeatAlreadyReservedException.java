package com.example.cinema.api.domain.order.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class SeatAlreadyReservedException extends DomainException {
    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
