package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieDescriptionRequiredException extends ValidationException {
    public MovieDescriptionRequiredException(String message) {
        super(message);
    }
}
