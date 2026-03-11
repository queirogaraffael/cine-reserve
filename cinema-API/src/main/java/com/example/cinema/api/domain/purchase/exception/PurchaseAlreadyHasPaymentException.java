package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class PurchaseAlreadyHasPaymentException extends ConflictException {
    public PurchaseAlreadyHasPaymentException(String message) {
        super(message);
    }
}
