package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class CinemaLocationRequiredException extends ValidationException {
    public CinemaLocationRequiredException(String message) {
        super(message);
    }
}
