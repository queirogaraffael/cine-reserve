package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class MovieRequiredException extends BadRequestException {
    public MovieRequiredException(String message) {
        super(message);
    }
}
