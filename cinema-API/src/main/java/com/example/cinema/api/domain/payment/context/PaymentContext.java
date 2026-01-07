package com.example.cinema.api.domain.payment.context;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.strategy.PaymentStrategy;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentContext {

    private final Map<PaymentType, PaymentStrategy<?>> strategies;

    public PaymentContext(Set<PaymentStrategy<?>> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::getType,
                        Function.identity()
                ));
    }

    public PaymentResponseDTO execute(
            Purchase purchase,
            User user,
            PaymentRequestDTO paymentRequestDTO,
            String idempotencyKey
    ) {

        PaymentType paymentType = paymentRequestDTO.getPaymentType();
        PaymentStrategy<?> strategy = strategies.get(paymentType);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No payment strategy found for type: " + paymentType
            );
        }

        return strategy.process(purchase, user, paymentRequestDTO, idempotencyKey);
    }
}

