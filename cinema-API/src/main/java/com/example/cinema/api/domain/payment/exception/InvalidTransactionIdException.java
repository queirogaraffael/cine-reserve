package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidTransactionIdException extends BadRequestException {
    public InvalidTransactionIdException(String message) {
        super(message);
    }
}
