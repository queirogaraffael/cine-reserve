package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class MovieSessionNoLongerAvailableException extends StateConflictException {
    public MovieSessionNoLongerAvailableException(String message) {
        super(message);
    }
}
