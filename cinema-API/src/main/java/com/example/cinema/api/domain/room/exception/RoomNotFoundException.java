package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class RoomNotFoundException extends ResourceNotFoundException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
