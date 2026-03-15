package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class TransactionAlreadyRegisteredException extends StateConflictException {
    public TransactionAlreadyRegisteredException(String message) {
        super(message);
    }
}
