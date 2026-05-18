package com.example.cinema.api.application.dto.payment;

import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.domain.purchase.Purchase;

import java.math.BigDecimal;

public record PaymentPurchaseContext(String idempotencyKey, BigDecimal totalPrice, Long id) {

    public PaymentPurchaseContext {
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new PaymentValidationException("Chave de idempotência é obrigatória");
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) throw new PaymentValidationException("Valor da compra deve ser maior que zero");
        if (id == null) throw new PaymentValidationException("ID da compra é obrigatório");
    }

    public static PaymentPurchaseContext from(Purchase purchase) {
        return new PaymentPurchaseContext(
                purchase.getIdempotencyKey(),
                purchase.getTotalPrice(),
                purchase.getId()
        );
    }
}