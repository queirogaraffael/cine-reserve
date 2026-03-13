package com.example.cinema.api.application.exception;

import com.example.cinema.api.shared.exception.validation;

public class InvalidSeatNumberException extends validation {
    public InvalidSeatNumberException(String message) {
        super(message);
    }
}
