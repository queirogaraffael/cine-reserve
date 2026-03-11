package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class ReleaseDateRequiredException extends BadRequestException {
    public ReleaseDateRequiredException(String message) {
        super(message);
    }
}
