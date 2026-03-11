package com.example.cinema.api.domain.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class MovieSessionRequiredException extends BadRequestException {
    public MovieSessionRequiredException(String message) {
        super(message);
    }
}
