package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.application.dto.payment.PaymentAddressDTO;
import com.example.cinema.api.application.dto.payment.PaymentPurchaseContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentFixture {

    public static PaymentPurchaseContext validPurchaseContext() {
        return new PaymentPurchaseContext(
                "idempotency-key-123",
                new BigDecimal("50.00"),
                UUID.randomUUID()
        );
    }

    public static PaymentPurchaseContext zeroPurchaseContext() {
        return new PaymentPurchaseContext(
                "idempotency-key-123",
                BigDecimal.ZERO,
                UUID.randomUUID()
        );
    }

    public static PaymentUserContext validUserContext() {
        return new PaymentUserContext(
                "usuario@email.com",
                "12345678900",
                "Nome Teste"
        );
    }

    public static PixPaymentRequestDTO validPixRequest() {
        return new PixPaymentRequestDTO();
    }

    public static CardPaymentRequestDTO validCardRequest() {
        PaymentAddressDTO address = new PaymentAddressDTO(
                "58000000",
                "Rua Teste",
                "123",
                "Centro",
                "João Pessoa",
                "PB"
        );
        return new CardPaymentRequestDTO(
                "visa",
                "card-token-abc",
                1,
                address
        );
    }
}
