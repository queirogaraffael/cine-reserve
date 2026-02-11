package com.example.cinema.api.shared.dtos.payment.response;

import com.example.cinema.api.domain.payment.PaymentStatus;

public interface PaymentResponseDTO {
    Long getPaymentId();
    void setPaymentId(Long paymentId);
    PaymentStatus getPaymentStatus();
}