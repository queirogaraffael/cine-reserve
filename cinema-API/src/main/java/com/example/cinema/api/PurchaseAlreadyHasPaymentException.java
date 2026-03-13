package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class PurchaseAlreadyHasPaymentException extends StateConflictException {
    public PurchaseAlreadyHasPaymentException(String message) {
        super(message);
    }
}
