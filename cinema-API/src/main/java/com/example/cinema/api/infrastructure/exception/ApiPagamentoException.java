package com.example.cinema.api.infrastructure.exception;

public class ApiPagamentoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiPagamentoException(String message, Exception exception) {
        super(message, exception);
    }
}
