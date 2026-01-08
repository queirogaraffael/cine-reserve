package com.example.cinema.api.shared.dtos.payment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixPaymentResponseDTO implements PaymentResponseDTO {

    private Long transactionId;
    private String paymentStatus;

    private String pixCopiaECola;
    private String qrCodeBase64;
    private String instrucoesUrl;

    private ZonedDateTime expirationDate;

    @Override
    public Long getTransactionId() {
        return transactionId;
    }

    @Override
    public String getPaymentStatus() {
        return paymentStatus;
    }
}