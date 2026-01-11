package com.example.cinema.api.shared.dtos.payment.response;


import com.example.cinema.api.domain.enums.PaymentStatus;
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
    private Long transactionId;
    private PaymentStatus paymentStatus;

    private String statusDetail;

    private String lastFourDigits;
    private Integer installments;
    private String paymentMethodId;
}