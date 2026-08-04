package com.example.cinema.api.infrastructure.messaging.rabbitmq.exception;

public class MaxRetriesExceededException extends RuntimeException {
    public MaxRetriesExceededException(String providerPaymentId) {
        super("Número máximo de tentativas excedido para o pagamento " + providerPaymentId);
    }
}
