package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.application.dto.payment.PaymentAddressDTO;
import com.example.cinema.api.application.dto.payment.PaymentOrderContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;

import java.math.BigDecimal;

public class PaymentFixture {

    public static PaymentOrderContext validPurchaseContext() {
        return new PaymentOrderContext(
                "idempotency-key-123",
                new BigDecimal("50.00"),
                1L
        );
    }

    public static PaymentOrderContext zeroPurchaseContext() {
        return new PaymentOrderContext(
                "idempotency-key-123",
                BigDecimal.ZERO,
                1L
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
