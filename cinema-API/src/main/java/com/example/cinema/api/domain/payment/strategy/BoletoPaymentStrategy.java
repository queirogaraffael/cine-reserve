package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.PaymentGatewayInterface;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentBoletoRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;

public class BoletoPaymentStrategy implements PaymentStrategy{

    private final PaymentGatewayInterface paymentGatewayInterface;

    public BoletoPaymentStrategy(PaymentGatewayInterface paymentGatewayInterface) {
        this.paymentGatewayInterface = paymentGatewayInterface;
    }

    @Override
    public PaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO paymentRequestDTO, String idempotencyKey) {

        PaymentBoletoRequestDTO boletoDetails = (PaymentBoletoRequestDTO) paymentRequestDTO;

        return paymentGatewayInterface.createBoletoPayment(purchase, user, boletoDetails, idempotencyKey);
    }

    @Override
    public PaymentType getType() {
        return PaymentType.BOLETO;
    }
}
