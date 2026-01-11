package com.example.cinema.api.shared.dtos.payment.response;

import com.example.cinema.api.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixPaymentResponseDTO implements PaymentResponseDTO {

    private Long paymentId;
    private Long transactionId;
    private PaymentStatus paymentStatus;

    private String pixCopiaECola;
    private String qrCodeBase64;
    private String instrucoesUrl;

    private ZonedDateTime expirationDate;
}