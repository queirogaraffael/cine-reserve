package com.example.cinema.api.domain.payment.context;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.strategy.PaymentStrategy;
import com.example.cinema.api.shared.dtos.payment.requests.*;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentContext {


    private final Map<PaymentType, PaymentStrategy> strategies;
    private final ObjectMapper objectMapper;

    public PaymentContext(Set<PaymentStrategy> strategies, ObjectMapper objectMapper) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getType, Function.identity()));
        this.objectMapper = objectMapper;
    }

    public PaymentResponseDTO executeStrategy(Purchase purchase, User user, PaymentMasterDTO paymentMasterDTO, String idempotencyKey){

        PaymentRequestDTO details = createSpecificRequestDTO(paymentMasterDTO);

        PaymentStrategy strategy = strategies.get(details.getPaymentType());

        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for payment type: " + details.getPaymentType());
        }

        return strategy
                .process(purchase, user, details, idempotencyKey);
    }

    private PaymentRequestDTO createSpecificRequestDTO(PaymentMasterDTO masterDTO) {

        Object rawDetails = masterDTO.getPaymentDetails();
        Class<? extends PaymentRequestDTO> targetClass = switch (masterDTO.getPaymentMethod()) {
            case PIX -> PixPaymentRequestDTO.class;
            case CARD -> CardPaymentRequestDTO.class;
            case BOLETO -> BoletoPaymentRequestDTO.class;
        };

        return objectMapper.convertValue(rawDetails, targetClass);
    }
}