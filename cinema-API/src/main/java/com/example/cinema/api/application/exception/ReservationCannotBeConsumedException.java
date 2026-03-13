package com.example.cinema.api.application.exception;

public class ReservationCannotBeConsumedException extends IllegalStateException
{
    public ReservationCannotBeConsumedException(String message) {
        super(message);
    }
}
