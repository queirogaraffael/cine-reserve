package com.example.cinema.api.domain.confirmacao.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class ConfirmacaoNotFoundException extends ResourceNotFoundException {
    public ConfirmacaoNotFoundException(String message) {
        super(message);
    }
}
