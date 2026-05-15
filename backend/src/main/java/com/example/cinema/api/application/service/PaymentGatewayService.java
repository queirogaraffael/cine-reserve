package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.PaymentPurchaseContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;

public interface PaymentGatewayService {
    PixGatewayResult createPixPayment(PaymentPurchaseContext purchase, PaymentUserContext user, PixPaymentRequestDTO request);
    CardGatewayResult createCardPayment(PaymentPurchaseContext purchase, PaymentUserContext user, CardPaymentRequestDTO request);
}