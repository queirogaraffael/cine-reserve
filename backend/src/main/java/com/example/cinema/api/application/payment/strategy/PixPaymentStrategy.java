package com.example.cinema.api.application.payment.strategy;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.service.gateway.PixPaymentGatewayPort;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PixPaymentStrategy implements PaymentStrategy<PixPaymentRequestDTO> {

    private final PixPaymentGatewayPort paymentGatewayService;

    public PixPaymentStrategy(PixPaymentGatewayPort paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @Override
    public PaymentType getType() {
        return PaymentType.PIX;
    }

    @Override
    public Class<PixPaymentRequestDTO> getRequestType() {
        return PixPaymentRequestDTO.class;
    }

    @Override
    public PaymentGatewayResult process(OrderPaymentContext purchase, PaymentUserContext user, PixPaymentRequestDTO request) {
        return paymentGatewayService.createPixPayment(purchase, user, request);
    }
}
