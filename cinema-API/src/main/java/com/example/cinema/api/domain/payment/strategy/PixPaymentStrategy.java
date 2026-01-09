package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.services.PaymentGatewayService;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PixPaymentStrategy implements PaymentStrategy<PixPaymentRequestDTO> {

    private final PaymentGatewayService paymentGatewayService;

    public PixPaymentStrategy(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @Override
    public PaymentType getType() {
        return PaymentType.PIX;
    }

    @Override
    public PaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO request) {
        if (!(request instanceof PixPaymentRequestDTO pixRequest)) {
            throw new IllegalArgumentException("Request is not a PixPaymentRequestDTO");
        }

        return paymentGatewayService.createPixPayment(
                purchase,
                user,
                pixRequest
        );
    }
}
