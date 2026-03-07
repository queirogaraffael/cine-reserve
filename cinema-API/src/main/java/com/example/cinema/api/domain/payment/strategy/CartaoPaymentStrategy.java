package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.service.PaymentGatewayService;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardPaymentResponseDTO;
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
    public CardPaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO request, Payment payment) {
        if (!(request instanceof CardPaymentRequestDTO cardRequest)) {
            throw new IllegalArgumentException("Request não é um CardPaymentRequestDTO");
        }

        CardGatewayResult result = paymentGatewayService.createCardPayment(purchase, user, cardRequest);

        payment.registerTransaction(result.getTransactionId());

        payment.updateStatus(PaymentStatus.fromValue(result.getStatus()), result.getStatusDetail());

        return CardPaymentResponseDTO.builder()
                .paymentStatus(PaymentStatus.valueOf(result.getStatus()))
                .statusDetail(result.getStatusDetail())
                .lastFourDigits(result.getLastFourDigits())
                .installments(result.getInstallments())
                .paymentMethodId(result.getPaymentMethodId())
                .build();

    }

}
