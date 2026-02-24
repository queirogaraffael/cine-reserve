package com.example.cinema.api.infrastructure.exception;

public class WebhookException extends RuntimeException {
    public WebhookException(String message, Exception exception) {
        super(message, exception);

    }
}
