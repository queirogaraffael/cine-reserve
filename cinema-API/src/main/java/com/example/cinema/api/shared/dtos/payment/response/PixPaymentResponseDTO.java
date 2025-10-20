package com.example.cinema.api.shared.dtos.payment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixPaymentResponseDTO implements PaymentResponseDTO{
    private Long transactionId;
    private String paymentStatus;
    private String pixCopiaECola;
    private String qrCodeBase64;
    private String instrucoesUrl;
}
