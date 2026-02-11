package com.example.cinema.api.shared.dtos.payment.response.gateway.pix;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
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
    private PaymentStatus paymentStatus;

    private String pixCopiaECola;
    private String qrCode;
    private String qrCodeBase64;
    private String instrucoesUrl;

    private ZonedDateTime expirationDate;
}