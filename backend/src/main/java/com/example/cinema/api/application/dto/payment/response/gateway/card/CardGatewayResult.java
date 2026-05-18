package com.example.cinema.api.application.dto.payment.response.gateway.card;

import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentStatus;

public record CardGatewayResult(Long transactionId, String status,
                                String statusDetail,
                                String lastFourDigits,
                                Integer installments,
                                String paymentMethodId) implements PaymentGatewayResult {

    @Override
    public PaymentResponseDTO toResponseDTO(Long paymentId) {
        return CardPaymentResponseDTO.builder()
                .paymentId(paymentId)
                .paymentStatus(PaymentStatus.fromValue(status))
                .statusDetail(statusDetail)
                .lastFourDigits(lastFourDigits)
                .installments(installments)
                .paymentMethodId(paymentMethodId)
                .build();
    }
}