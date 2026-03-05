package com.example.cinema.api.domain.movie.exception;

public class DuracaoFilmeInvalidaException extends RuntimeException {
    public DuracaoFilmeInvalidaException(String message) {
        super(message);
    }
}
