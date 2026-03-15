package com.example.cinema.api;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidTransactionIdException extends ValidationException {
    public InvalidTransactionIdException(String message) {
        super(message);
    }
}
