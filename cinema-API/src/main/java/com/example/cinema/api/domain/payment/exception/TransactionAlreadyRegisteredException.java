package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class TransactionAlreadyRegisteredException extends StateConflictException {
    public TransactionAlreadyRegisteredException(String message) {
        super(message);
    }
}
