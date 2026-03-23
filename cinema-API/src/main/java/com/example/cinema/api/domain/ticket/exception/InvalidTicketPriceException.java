package com.example.cinema.api.domain.ticket.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidTicketPriceException extends ValidationException {
    public InvalidTicketPriceException(String message) {
        super(message);
    }
}
