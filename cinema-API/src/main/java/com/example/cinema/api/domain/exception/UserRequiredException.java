package com.example.cinema.api.domain.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class UserRequiredException extends BadRequestException {
    public UserRequiredException(String message) {
        super(message);
    }
}
