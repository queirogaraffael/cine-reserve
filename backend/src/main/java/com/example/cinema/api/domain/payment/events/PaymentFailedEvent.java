package com.example.cinema.api.domain.payment.events;

public record PaymentFailedEvent(Long orderPaymentId, Long orderId, String reason) {
}
