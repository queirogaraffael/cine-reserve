package com.example.cinema.api.domain.payment.events;

import com.example.cinema.api.domain.payment.PaymentStatus;

public record PixPaymentStatusUpdatedEvent(
    Long orderId,
    PaymentStatus status,
    String detail
) {}
