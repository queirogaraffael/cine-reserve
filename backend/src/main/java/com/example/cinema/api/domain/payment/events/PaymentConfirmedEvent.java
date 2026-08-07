package com.example.cinema.api.domain.payment.events;

public record PaymentConfirmedEvent(Long orderPaymentId, Long orderId) {
}
