package com.example.cinema.api.shared.dtos.payment.response;

import com.example.cinema.api.domain.enums.PaymentStatus;

public interface PaymentResponseDTO {
    Long getPaymentId();
    void setPaymentId(Long paymentId);
    Long getTransactionId();
    PaymentStatus getPaymentStatus();
}