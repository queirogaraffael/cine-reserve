package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class PurchaseRequiredException extends ValidationException {
    public PurchaseRequiredException(String message) {
        super(message);
    }
}
