package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidMovieDurationException extends BadRequestException {
    public InvalidMovieDurationException(String message) {
        super(message);
    }
}
