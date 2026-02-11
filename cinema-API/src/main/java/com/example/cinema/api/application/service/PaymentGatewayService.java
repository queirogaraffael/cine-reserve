package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.shared.dtos.payment.response.gateway.pix.PixGatewayResult;

public interface PaymentGatewayService {
    PixGatewayResult createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request);
    CardGatewayResult createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request);
}