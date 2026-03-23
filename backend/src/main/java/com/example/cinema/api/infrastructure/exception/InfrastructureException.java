package com.example.cinema.api.infrastructure.exception;

public abstract class InfrastructureException extends RuntimeException {
    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
