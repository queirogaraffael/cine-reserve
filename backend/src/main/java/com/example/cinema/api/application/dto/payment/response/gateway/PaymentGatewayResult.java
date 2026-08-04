package com.example.cinema.api.application.dto.payment.response.gateway;

import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;

public interface PaymentGatewayResult {
    String providerPaymentId();
    String status();
    String statusDetail();
    PaymentResponseDTO toResponseDTO(Long paymentId);
}