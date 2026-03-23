package com.example.cinema.api.domain.seatreservation.exception;

public class ReservationCannotBeConsumedException extends IllegalStateException
{
    public ReservationCannotBeConsumedException(String message) {
        super(message);
    }
}
