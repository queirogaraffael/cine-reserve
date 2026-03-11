package com.example.cinema.api.application.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidSeatNumberException extends BadRequestException {
    public InvalidSeatNumberException(String message) {
        super(message);
    }
}
