package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidPaymentStatusException extends ValidationException {
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
