package com.example.cinema.api.shared.dtos.payment.response.gateway.card;


import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponseDTO implements PaymentResponseDTO {

    private Long paymentId;
    private PaymentStatus paymentStatus;

    private String statusDetail;

    private String lastFourDigits;
    private Integer installments;
    private String paymentMethodId;
}