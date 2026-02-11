package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;

public interface PaymentRequestDTO {
    PaymentType getPaymentType();
}