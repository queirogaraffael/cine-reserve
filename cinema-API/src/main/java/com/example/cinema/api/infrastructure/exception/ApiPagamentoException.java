package com.example.cinema.api.infrastructure.exception;

public class ApiPagamentoException extends InfrastructureException {
    public ApiPagamentoException(String message, Exception exception) {
        super(message, exception);
    }
}
