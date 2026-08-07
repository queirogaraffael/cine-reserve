package com.example.cinema.api.application.dto.payment;

import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.domain.order.Order;

import java.math.BigDecimal;

public record OrderPaymentContext(String idempotencyKey, BigDecimal totalPrice, Long id, Long orderPaymentId) {

    public OrderPaymentContext {
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new PaymentValidationException("Chave de idempotência é obrigatória");
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) throw new PaymentValidationException("Valor da compra deve ser maior que zero");
        if (id == null) throw new PaymentValidationException("ID da compra é obrigatório");
        if (orderPaymentId == null) throw new PaymentValidationException("ID do pagamento é obrigatório");
    }

    public static OrderPaymentContext from(Order order, Long orderPaymentId) {
        return new OrderPaymentContext(
                "orderPayment-" + orderPaymentId,
                order.getTotalPrice().getAmount(),
                order.getId(),
                orderPaymentId
        );
    }
}