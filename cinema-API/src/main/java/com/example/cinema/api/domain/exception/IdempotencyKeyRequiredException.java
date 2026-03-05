package com.example.cinema.api.domain.exception;

public class IdempotencyKeyRequiredException extends RuntimeException {
    public IdempotencyKeyRequiredException(String message) {
        super(message);
    }
}
