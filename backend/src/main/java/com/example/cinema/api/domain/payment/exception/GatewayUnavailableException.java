package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.InfrastructureUnavailableException;

public class GatewayUnavailableException extends InfrastructureUnavailableException {
    public GatewayUnavailableException(String message) {
        super(message);
    }
}
