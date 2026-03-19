package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class MovieSessionNotAvailableException extends StateConflictException {
    public MovieSessionNotAvailableException(String message) {
        super(message);
    }
}
