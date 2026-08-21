package com.example.cinema.api.application.dto.payment.response;

import com.example.cinema.api.domain.payment.PaymentStatus;

public record PaymentStatusUpdateEventDTO(
    Long orderId,
    PaymentStatus paymentStatus,
    String statusDetail
) {}
