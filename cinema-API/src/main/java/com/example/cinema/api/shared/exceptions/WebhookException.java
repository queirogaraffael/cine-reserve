package com.example.cinema.api.shared.exceptions;

public class WebhookException extends RuntimeException {
    public WebhookException(String message, Exception exception) {
        super(message, exception);

    }
}
