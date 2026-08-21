package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class RetryNotAllowedException extends StateConflictException {
    public RetryNotAllowedException(String message) {
        super(message);
    }
}
