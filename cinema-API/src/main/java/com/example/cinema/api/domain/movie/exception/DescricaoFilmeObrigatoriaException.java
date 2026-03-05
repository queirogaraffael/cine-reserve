package com.example.cinema.api.domain.movie.exception;

public class DescricaoFilmeObrigatoriaException extends RuntimeException {
    public DescricaoFilmeObrigatoriaException(String message) {
        super(message);
    }
}
