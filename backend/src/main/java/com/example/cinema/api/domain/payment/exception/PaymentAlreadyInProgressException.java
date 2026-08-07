package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class PaymentAlreadyInProgressException extends StateConflictException {
    public PaymentAlreadyInProgressException(String message) {
        super(message);
    }
}
