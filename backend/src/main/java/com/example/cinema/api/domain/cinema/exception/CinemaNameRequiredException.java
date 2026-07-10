package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class CinemaNameRequiredException extends ValidationException {
    public CinemaNameRequiredException(String message) {
        super(message);
    }
}
