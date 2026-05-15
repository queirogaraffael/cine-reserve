package com.example.cinema.api.application.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentPurchaseContext(
        String idempotencyKey,
        BigDecimal totalPrice,
        UUID id
) {
    public PaymentPurchaseContext {
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new ValidationException("Chave de idempotência é obrigatória");
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) throw new ValidationException("Valor da compra deve ser maior que zero");
        if (id == null) throw new ValidationException("ID da compra é obrigatório");
    }
}