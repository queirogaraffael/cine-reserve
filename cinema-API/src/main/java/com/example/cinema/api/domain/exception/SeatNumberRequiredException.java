package com.example.cinema.api.domain.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class SeatNumberRequiredException extends BadRequestException {
    public SeatNumberRequiredException(String message) {
        super(message);
    }
}
