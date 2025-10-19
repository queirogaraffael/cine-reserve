package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentBoletoRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;

public interface PaymentGatewayInterface {
    PaymentResponseDTO createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request, String idempotencyKey);
    PaymentResponseDTO createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request, String idempotencyKey);
    PaymentResponseDTO createBoletoPayment(Purchase purchase, User user, PaymentBoletoRequestDTO paymentBoletoRequestDTO, String idempotencyKey);
}