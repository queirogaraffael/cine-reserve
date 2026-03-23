package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieGenreRequiredException extends ValidationException {
    public MovieGenreRequiredException(String message) {
        super(message);
    }
}
