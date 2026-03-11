package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class MovieGenreRequiredException extends BadRequestException {
    public MovieGenreRequiredException(String message) {
        super(message);
    }
}
