package com.example.cinema.api.application.service.gateway;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;

public interface CardPaymentGatewayPort {
    CardGatewayResult createCardPayment(OrderPaymentContext purchase, PaymentUserContext user, CardPaymentRequestDTO request);
}
