package com.example.cinema.api.application.service.gateway;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;

public interface PixPaymentGatewayPort {
    PixGatewayResult createPixPayment(OrderPaymentContext purchase, PaymentUserContext user, PixPaymentRequestDTO request);
}
