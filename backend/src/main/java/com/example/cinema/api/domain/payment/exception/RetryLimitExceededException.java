package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class RetryLimitExceededException extends StateConflictException {
    public RetryLimitExceededException(String message) {
        super(message);
    }
}
