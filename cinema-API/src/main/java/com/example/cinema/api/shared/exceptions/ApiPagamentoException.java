package com.example.cinema.api.shared.exceptions;

public class ApiPagamentoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiPagamentoException(String message, Exception exception) {
        super(message, exception);
    }
}
