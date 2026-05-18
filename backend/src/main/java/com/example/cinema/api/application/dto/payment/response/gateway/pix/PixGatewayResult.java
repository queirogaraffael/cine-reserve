package com.example.cinema.api.application.dto.payment.response.gateway.pix;

import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentStatus;

import java.time.ZonedDateTime;

public record PixGatewayResult(Long transactionId,
                               String status,
                               String statusDetail,
                               String pixCopiaECola,
                               String qrCodeBase64,
                               String instrucoesUrl,
                               ZonedDateTime
                               expirationDate) implements PaymentGatewayResult {

    @Override
    public PaymentResponseDTO toResponseDTO(Long paymentId) {
        return PixPaymentResponseDTO.builder()
                .paymentId(paymentId)
                .paymentStatus(PaymentStatus.fromValue(status))
                .statusDetail(statusDetail)
                .pixCopiaECola(pixCopiaECola)
                .qrCodeBase64(qrCodeBase64)
                .instrucoesUrl(instrucoesUrl)
                .expirationDate(expirationDate)
                .build();
    }
}