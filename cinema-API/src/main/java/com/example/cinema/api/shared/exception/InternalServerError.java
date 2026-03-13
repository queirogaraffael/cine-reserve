package com.example.cinema.api.shared.exception;

public class InternalServerError extends RuntimeException {
    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }
}
