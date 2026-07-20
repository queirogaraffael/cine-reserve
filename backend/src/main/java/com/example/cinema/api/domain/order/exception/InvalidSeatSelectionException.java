package com.example.cinema.api.domain.order.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidSeatSelectionException extends ValidationException {
    public InvalidSeatSelectionException(String message) {
        super(message);
    }
}
