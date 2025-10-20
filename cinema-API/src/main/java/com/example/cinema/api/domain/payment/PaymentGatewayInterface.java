package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.BoletoPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.BoletoPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.CardPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PixPaymentResponseDTO;

public interface PaymentGatewayInterface {
    PixPaymentResponseDTO createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request, String idempotencyKey);
    CardPaymentResponseDTO createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request, String idempotencyKey);
    BoletoPaymentResponseDTO createBoletoPayment(Purchase purchase, User user, BoletoPaymentRequestDTO paymentBoletoRequestDTO, String idempotencyKey);
}