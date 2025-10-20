package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.enums.PaymentType;

import java.math.BigDecimal;

public class PixPaymentRequestDTO implements PaymentRequestDTO {

    private BigDecimal amount;
    private String payerEmail;
    private String cpfCnpj;

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.PIX;
    }
}