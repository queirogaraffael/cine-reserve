package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class ReleaseDateRequiredException extends ValidationException {
    public ReleaseDateRequiredException(String message) {
        super(message);
    }
}
