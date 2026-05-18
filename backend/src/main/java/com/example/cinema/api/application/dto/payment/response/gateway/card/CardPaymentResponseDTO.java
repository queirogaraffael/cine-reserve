package com.example.cinema.api.application.dto.payment.response.gateway.card;


import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import lombok.*;

@Value
@Builder
public class CardPaymentResponseDTO implements PaymentResponseDTO {

    Long paymentId;
    PaymentStatus paymentStatus;
    String statusDetail;
    String lastFourDigits;
    Integer installments;
    String paymentMethodId;
}