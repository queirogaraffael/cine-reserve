package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;

public interface PaymentStrategy<T extends PaymentRequestDTO> {

    PaymentType getType();

    PaymentResponseDTO process(
            Purchase purchase,
            User user,
            PaymentRequestDTO request
    );
}
