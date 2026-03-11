package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
