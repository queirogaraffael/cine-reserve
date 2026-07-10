package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class CinemaNotFoundException extends ResourceNotFoundException {
    public CinemaNotFoundException(String message) {
        super(message);
    }
}
