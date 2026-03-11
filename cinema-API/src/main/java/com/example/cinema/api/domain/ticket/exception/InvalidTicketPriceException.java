package com.example.cinema.api.domain.ticket.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class InvalidTicketPriceException extends BadRequestException {
    public InvalidTicketPriceException(String message) {
        super(message);
    }
}
