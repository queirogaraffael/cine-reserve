package com.example.cinema.api.shared.dtos.payment.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponseDTO implements PaymentResponseDTO {

    private Long transactionId;
    private String paymentStatus;

    private String statusDetail;

    private String lastFourDigits;
    private Integer installments;
    private String paymentMethodId;

    @Override
    public Long getTransactionId() {
        return transactionId;
    }

    @Override
    public String getPaymentStatus() {
        return paymentStatus;
    }
}