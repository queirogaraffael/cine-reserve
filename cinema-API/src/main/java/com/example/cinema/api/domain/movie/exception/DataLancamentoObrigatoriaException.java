package com.example.cinema.api.domain.movie.exception;

public class DataLancamentoObrigatoriaException extends RuntimeException {
    public DataLancamentoObrigatoriaException(String message) {
        super(message);
    }
}
