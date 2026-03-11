package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class TransactionAlreadyRegisteredException extends ConflictException {
    public TransactionAlreadyRegisteredException(String message) {
        super(message);
    }
}
