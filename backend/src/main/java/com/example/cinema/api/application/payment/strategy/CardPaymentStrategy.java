package com.example.cinema.api.application.payment.strategy;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.service.gateway.CardPaymentGatewayPort;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy<CardPaymentRequestDTO> {

    private final CardPaymentGatewayPort paymentGatewayService;

    public CardPaymentStrategy(CardPaymentGatewayPort paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @Override
    public PaymentType getType() {
        return PaymentType.CARD;
    }

    @Override
    public Class<CardPaymentRequestDTO> getRequestType() {
        return CardPaymentRequestDTO.class;
    }

    @Override
    public PaymentGatewayResult process(OrderPaymentContext purchase, PaymentUserContext user, CardPaymentRequestDTO request) {
        return paymentGatewayService.createCardPayment(purchase, user, request);
    }
}
