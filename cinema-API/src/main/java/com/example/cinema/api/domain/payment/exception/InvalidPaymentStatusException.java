package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidPaymentStatusException extends BadRequestException {
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
