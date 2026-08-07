package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;

public interface PaymentGatewayService {
    PixGatewayResult createPixPayment(OrderPaymentContext purchase, PaymentUserContext user, PixPaymentRequestDTO request);
    CardGatewayResult createCardPayment(OrderPaymentContext purchase, PaymentUserContext user, CardPaymentRequestDTO request);
}
