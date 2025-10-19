package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.PaymentGatewayInterface;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;

public class PixPaymentStrategy implements PaymentStrategy {

    private final PaymentGatewayInterface paymentGatewayInterface;

    public PixPaymentStrategy(PaymentGatewayInterface paymentGatewayInterface) {
        this.paymentGatewayInterface = paymentGatewayInterface;
    }

    public PaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO details, String idempotencyKey)  {

        PixPaymentRequestDTO pixDetails = (PixPaymentRequestDTO) details;

        return paymentGatewayInterface.createPixPayment(purchase, user, pixDetails, idempotencyKey);

    }

    @Override
    public PaymentType getType() {
        return PaymentType.PIX;
    }
}