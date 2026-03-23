package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;

public interface PaymentStrategy<T extends PaymentRequestDTO> {

    PaymentType getType();

    PaymentResponseDTO process(
            Purchase purchase,
            User user,
            PaymentRequestDTO request,
            Payment payment
    );
}
