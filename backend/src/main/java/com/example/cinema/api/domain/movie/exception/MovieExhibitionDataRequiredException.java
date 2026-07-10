package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieExhibitionDataRequiredException extends ValidationException {
    public MovieExhibitionDataRequiredException(String message) {
        super(message);
    }
}
