package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidTransactionIdException extends ValidationException {
    public InvalidTransactionIdException(String message) {
        super(message);
    }
}
