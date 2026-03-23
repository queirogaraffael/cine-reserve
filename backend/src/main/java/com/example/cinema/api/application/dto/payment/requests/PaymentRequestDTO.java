package com.example.cinema.api.application.dto.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;

public interface PaymentRequestDTO {
    PaymentType getPaymentType();
}