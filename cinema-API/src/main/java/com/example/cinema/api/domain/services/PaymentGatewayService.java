package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.CardPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PixPaymentResponseDTO;

public interface PaymentGatewayService {
    PixPaymentResponseDTO createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request);
    CardPaymentResponseDTO createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request);
}