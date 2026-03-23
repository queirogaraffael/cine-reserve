package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieTitleRequiredException extends ValidationException {
    public MovieTitleRequiredException(String message) {
        super(message);
    }
}
