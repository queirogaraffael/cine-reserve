package com.example.cinema.api.domain.room.exception;


import com.example.cinema.api.shared.exception.BadRequestException;

public class RoomRequiredException extends BadRequestException {
    public RoomRequiredException(String message) {
        super(message);
    }
}
