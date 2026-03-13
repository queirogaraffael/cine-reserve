package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class InvalidSessionTimeRangeException extends StateConflictException {
    public InvalidSessionTimeRangeException(String message) {
        super(message);
    }
}
