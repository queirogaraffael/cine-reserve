package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class PaymentMethodRequiredException extends BadRequestException {
    public PaymentMethodRequiredException(String message) {
        super(message);
    }
}
