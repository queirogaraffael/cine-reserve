package com.example.cinema.api.application.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class RoomScheduleConflictException extends ConflictException {
    public RoomScheduleConflictException(String message) {
        super(message);
    }
}
