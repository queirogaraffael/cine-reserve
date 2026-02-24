package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.service.PaymentGatewayService;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixPaymentResponseDTO;
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
    public PixPaymentResponseDTO process(Purchase purchase, User user, PaymentRequestDTO request, Payment payment) {
        if (!(request instanceof PixPaymentRequestDTO pixRequest)) {
            throw new IllegalArgumentException("Request não é um PixPaymentRequestDTO");
        }

        PixGatewayResult result = paymentGatewayService.createPixPayment(purchase, user, pixRequest);

        payment.setTransactionId(result.getTransactionId());
        payment.setPaymentStatus(PaymentStatus.fromValue(result.getStatus()));
        payment.setStatusDetail(result.getStatusDetail());

        return PixPaymentResponseDTO.builder()
                .paymentStatus(payment.getPaymentStatus())
                .pixCopiaECola(result.getPixCopiaECola())
                .qrCode(result.getQrCode())
                .qrCodeBase64(result.getQrCodeBase64())
                .instrucoesUrl(result.getInstrucoesUrl())
                .expirationDate(result.getExpirationDate())
                .build();
    }
}
