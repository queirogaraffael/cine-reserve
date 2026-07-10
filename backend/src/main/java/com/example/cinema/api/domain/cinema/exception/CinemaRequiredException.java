package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class CinemaRequiredException extends ValidationException {
    public CinemaRequiredException(String message) {
        super(message);
    }
}
