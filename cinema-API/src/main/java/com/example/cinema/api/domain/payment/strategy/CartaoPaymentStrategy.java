package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.PaymentGatewayInterface;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;

public class CartaoPaymentStrategy implements PaymentStrategy{

    private final PaymentGatewayInterface paymentGatewayInterface;

    public CartaoPaymentStrategy(PaymentGatewayInterface paymentGatewayInterface) {
        this.paymentGatewayInterface = paymentGatewayInterface;
    }

    @Override
    public PaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO paymentRequestDTO, String idempotencyKey) {

        CardPaymentRequestDTO cardDetails = (CardPaymentRequestDTO) paymentRequestDTO;

        return paymentGatewayInterface.createCardPayment(purchase, user, cardDetails, idempotencyKey);
    }

    @Override
    public PaymentType getType() {
        return PaymentType.CARD;
    }
}
