package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieRequiredException extends ValidationException {
    public MovieRequiredException(String message) {
        super(message);
    }
}
