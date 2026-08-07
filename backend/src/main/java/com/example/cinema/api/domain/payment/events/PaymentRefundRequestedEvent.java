package com.example.cinema.api.domain.payment.events;

import com.example.cinema.api.domain.common.Money;

public record PaymentRefundRequestedEvent(Long orderPaymentId, Long orderId, Money amount, String reason) {
}
