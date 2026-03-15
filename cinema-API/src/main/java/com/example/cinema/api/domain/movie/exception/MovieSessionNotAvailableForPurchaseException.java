package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class MovieSessionNotAvailableForPurchaseException extends StateConflictException {
    public MovieSessionNotAvailableForPurchaseException(String message) {
        super(message);
    }
}
