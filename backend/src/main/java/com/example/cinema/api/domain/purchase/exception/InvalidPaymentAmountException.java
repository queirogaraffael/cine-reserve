package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidPaymentAmountException extends ValidationException {
    public InvalidPaymentAmountException(String message) {
        super(message);
    }
}
