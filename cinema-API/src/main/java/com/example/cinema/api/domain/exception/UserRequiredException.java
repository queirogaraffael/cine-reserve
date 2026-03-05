package com.example.cinema.api.domain.exception;

public class UserRequiredException extends RuntimeException {
    public UserRequiredException(String message) {
        super(message);
    }
}
