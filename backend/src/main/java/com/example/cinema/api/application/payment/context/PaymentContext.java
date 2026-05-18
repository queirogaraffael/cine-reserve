package com.example.cinema.api.application.payment.context;

import com.example.cinema.api.application.dto.payment.PaymentPurchaseContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.payment.strategy.PaymentStrategy;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
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

    public PaymentGatewayResult execute(PaymentPurchaseContext purchase, PaymentUserContext user,
                                             PaymentRequestDTO request) {
        PaymentType paymentType = request.getPaymentType();
        PaymentStrategy<?> strategy = strategies.get(paymentType);

        if (strategy == null) {
            throw new IllegalArgumentException("No payment strategy found for type: " + paymentType);
        }

        return dispatch(strategy, purchase, user, request);
    }

    @SuppressWarnings("unchecked")
    private <T extends PaymentRequestDTO> PaymentGatewayResult dispatch(PaymentStrategy<T> strategy, PaymentPurchaseContext purchase,
                                                                        PaymentUserContext user,
                                                                        PaymentRequestDTO request) {
        T typed = strategy.getRequestType().cast(request);
        return strategy.process(purchase, user, typed);
    }
}