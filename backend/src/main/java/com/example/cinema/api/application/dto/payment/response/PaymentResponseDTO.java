package com.example.cinema.api.application.dto.payment.response;

import com.example.cinema.api.domain.payment.PaymentStatus;

public interface PaymentResponseDTO {
    Long getPaymentId();
    PaymentStatus getPaymentStatus();
}