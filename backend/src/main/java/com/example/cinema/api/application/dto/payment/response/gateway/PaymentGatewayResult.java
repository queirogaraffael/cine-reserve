package com.example.cinema.api.application.dto.payment.response.gateway;

import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;

public interface PaymentGatewayResult {
    Long transactionId();
    String status();
    String statusDetail();
    PaymentResponseDTO toResponseDTO(Long paymentId);
}