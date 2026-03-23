package com.example.cinema.api.domain.exception;

public abstract class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}