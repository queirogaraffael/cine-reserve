package com.example.cinema.api.application.dto.payment.response.gateway.pix;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import lombok.*;

import java.time.ZonedDateTime;

@Value
@Builder
public class PixPaymentResponseDTO implements PaymentResponseDTO {

    Long paymentId;
    PaymentStatus paymentStatus;
    String statusDetail;
    String pixCopiaECola;
    String qrCodeBase64;
    String instrucoesUrl;
    ZonedDateTime expirationDate;
}