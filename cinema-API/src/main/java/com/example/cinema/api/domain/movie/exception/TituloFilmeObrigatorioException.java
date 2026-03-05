package com.example.cinema.api.domain.movie.exception;

public class TituloFilmeObrigatorioException extends RuntimeException {
    public TituloFilmeObrigatorioException(String message) {
        super(message);
    }
}
