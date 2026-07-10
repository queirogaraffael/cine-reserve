package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class MovieRatingRequiredException extends ValidationException {
    public MovieRatingRequiredException(String message) {
        super(message);
    }
}
