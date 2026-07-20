package com.example.cinema.api.application.dto.payment;

import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.domain.order.Order;

import java.math.BigDecimal;

public record PaymentOrderContext(String idempotencyKey, BigDecimal totalPrice, Long id) {

    public PaymentOrderContext {
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new PaymentValidationException("Chave de idempotência é obrigatória");
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) throw new PaymentValidationException("Valor da compra deve ser maior que zero");
        if (id == null) throw new PaymentValidationException("ID da compra é obrigatório");
    }

    public static PaymentOrderContext from(Order order) {
        return new PaymentOrderContext(
                "order-" + order.getId(),
                order.getTotalPrice(),
                order.getId()
        );
    }
}