package com.example.cinema.api.application.payment.strategy;

import com.example.cinema.api.application.dto.payment.PaymentOrderContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;

public interface PaymentStrategy<T extends PaymentRequestDTO> {
    PaymentType getType();
    Class<T> getRequestType();
    PaymentGatewayResult process(PaymentOrderContext purchase, PaymentUserContext user, T request);
}
