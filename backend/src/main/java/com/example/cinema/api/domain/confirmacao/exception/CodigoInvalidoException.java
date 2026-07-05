package com.example.cinema.api.domain.confirmacao.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class CodigoInvalidoException extends DomainException {
    public CodigoInvalidoException(String message) {
        super(message);
    }
}
