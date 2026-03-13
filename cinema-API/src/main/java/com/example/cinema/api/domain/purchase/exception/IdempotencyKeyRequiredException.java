package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class IdempotencyKeyRequiredException extends ValidationException {
    public IdempotencyKeyRequiredException(String message) {
        super(message);
    }
}
