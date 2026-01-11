package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.services.PaymentGatewayService;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.gateway.PaymentGatewayResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CartaoPaymentStrategy implements PaymentStrategy<CardPaymentRequestDTO> {

    private final PaymentGatewayService paymentGatewayService;

    public CartaoPaymentStrategy(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @Override
    public PaymentType getType() {
        return PaymentType.CARD;
    }

    @Override
    public PaymentGatewayResponseDTO process(Purchase purchase, User user, PaymentRequestDTO request) {
        if (!(request instanceof CardPaymentRequestDTO cardRequest)) {
            throw new IllegalArgumentException("Request is not a CardPaymentRequestDTO");
        }

        return paymentGatewayService.createCardPayment(
                purchase,
                user,
                cardRequest
        );
    }
}
