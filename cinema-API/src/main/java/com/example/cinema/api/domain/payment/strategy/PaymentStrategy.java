package com.example.cinema.api.domain.payment.strategy;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.gateway.PaymentGatewayResponseDTO;

public interface PaymentStrategy<T extends PaymentRequestDTO> {

    PaymentType getType();

    PaymentGatewayResponseDTO process(
            Purchase purchase,
            User user,
            PaymentRequestDTO request
    );
}
