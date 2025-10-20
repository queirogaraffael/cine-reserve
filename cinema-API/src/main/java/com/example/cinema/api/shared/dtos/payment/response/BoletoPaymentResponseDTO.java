package com.example.cinema.api.shared.dtos.payment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletoPaymentResponseDTO implements PaymentResponseDTO{
    private Long transactionId;
    private String paymentStatus;
    private String boletoUrl;
    private String linhaDigitavel;
    private OffsetDateTime expirationDate;
}