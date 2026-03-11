package com.example.cinema.api.domain.genre.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class GenreNameRequiredException extends BadRequestException {
    public GenreNameRequiredException(String message) {
        super(message);
    }
}
