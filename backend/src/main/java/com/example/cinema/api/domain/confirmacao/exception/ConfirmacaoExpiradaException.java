package com.example.cinema.api.domain.confirmacao.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class ConfirmacaoExpiradaException extends DomainException {
    public ConfirmacaoExpiradaException(String message) {
        super(message);
    }
}
