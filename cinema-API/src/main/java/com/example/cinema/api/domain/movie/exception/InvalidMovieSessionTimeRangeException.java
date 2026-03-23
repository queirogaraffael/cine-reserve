package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class InvalidMovieSessionTimeRangeException extends StateConflictException {
    public InvalidMovieSessionTimeRangeException(String message) {
        super(message);
    }
}
