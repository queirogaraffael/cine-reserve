package com.example.cinema.api.application.dto.payment.response.gateway;

public interface PaymentGatewayResponseDTO {
    Long getTransactionId();
    String getStatus();
}