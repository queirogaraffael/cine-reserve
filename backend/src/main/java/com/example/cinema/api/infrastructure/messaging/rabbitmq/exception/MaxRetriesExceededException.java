package com.example.cinema.api.infrastructure.messaging.rabbitmq.exception;

public class MaxRetriesExceededException extends RuntimeException {
    public MaxRetriesExceededException(Long paymentId) {
        super("Max retries exceeded for payment " + paymentId);
    }
}
