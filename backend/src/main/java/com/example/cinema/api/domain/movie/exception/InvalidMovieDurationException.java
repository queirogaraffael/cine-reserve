package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidMovieDurationException extends ValidationException {
    public InvalidMovieDurationException(String message) {
        super(message);
    }
}
