package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class PurchaseRequiredException extends BadRequestException {
    public PurchaseRequiredException(String message) {
        super(message);
    }
}
