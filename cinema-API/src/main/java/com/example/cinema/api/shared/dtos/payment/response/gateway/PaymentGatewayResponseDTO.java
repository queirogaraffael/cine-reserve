package com.example.cinema.api.shared.dtos.payment.response.gateway;

public interface PaymentGatewayResponseDTO {
    Long getTransactionId();
    String getStatus();
}