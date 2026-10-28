package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.OrderAlreadyPaidException;
import com.example.cinema.api.domain.payment.exception.OrderNotEligibleForRetryException;
import com.example.cinema.api.domain.payment.exception.RetryLimitExceededException;
import com.example.cinema.api.domain.payment.exception.RetryNotAllowedException;
import com.example.cinema.api.domain.movie.exception.MovieSessionNoLongerAvailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentValidationService {

    @Value("${cinereserve.payment.retry.max-attempts:5}")
    private int maxRetryAttempts;

    @Value("${cinereserve.payment.retry.interval-seconds:30}")
    private int minRetryIntervalSeconds;

    public boolean isEligibleForRetry(Order order, OrderPayment latestPayment, long totalAttempts) {
        try {
            validateRetry(order, latestPayment, totalAttempts);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void validateRetry(Order order, OrderPayment latestPayment, long totalAttempts) {
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new OrderNotEligibleForRetryException(
                    "Pagamento não pode ser retentado: o pedido não está aguardando pagamento.");
        }

        if (latestPayment == null) {
            throw new RetryNotAllowedException("Nenhuma tentativa de pagamento anterior encontrada para este pedido.");
        }

        PaymentStatus status = latestPayment.getPaymentStatus();
        if (status == PaymentStatus.PENDING || status == PaymentStatus.IN_PROCESS
                || status == PaymentStatus.AUTHORIZED) {
            throw new RetryNotAllowedException("Aguarde o processamento do pagamento atual antes de tentar novamente.");
        }

        if (status == PaymentStatus.APPROVED) {
            throw new OrderAlreadyPaidException(
                    "Este pedido já foi pago com sucesso e não pode ser processado novamente.");
        }

        if (totalAttempts >= maxRetryAttempts) {
            throw new RetryLimitExceededException(
                    "Limite de " + maxRetryAttempts + " tentativas de pagamento atingido para este pedido.");
        }

        if (latestPayment.getPaymentDate().plusSeconds(minRetryIntervalSeconds).isAfter(LocalDateTime.now())) {
            throw new RetryNotAllowedException(
                    "Aguarde " + minRetryIntervalSeconds + " segundos antes de tentar novamente.");
        }

        order.getMovieSession().validateAvailabilityForPayment();
    }
}
