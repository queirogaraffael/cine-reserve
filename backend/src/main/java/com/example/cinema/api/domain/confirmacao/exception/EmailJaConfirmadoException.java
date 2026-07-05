package com.example.cinema.api.domain.confirmacao.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class EmailJaConfirmadoException extends DomainException {
    public EmailJaConfirmadoException(String message) {
        super(message);
    }
}
