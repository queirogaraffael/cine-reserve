package com.example.cinema.api.application.service;

import com.example.cinema.api.application.exception.InvalidPaymentSnapshotException;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.payment.events.PaymentConfirmedEvent;
import com.example.cinema.api.domain.payment.events.PixPaymentStatusUpdatedEvent;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentUpdateService {

    private final OrderPaymentRepositoryJpa paymentRepositoryJpa;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentUpdateService(OrderPaymentRepositoryJpa paymentRepositoryJpa, ApplicationEventPublisher eventPublisher) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processPaymentUpdate(ExternalPaymentSnapshot snapshot) {

        if (snapshot.externalReference() == null) {
            throw new InvalidPaymentSnapshotException("Pagamento sem external_reference");
        }

        Long orderPaymentId = snapshot.externalReference();

        OrderPayment payment = paymentRepositoryJpa.findById(orderPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Pagamento nao encontrado para orderPaymentId: " + orderPaymentId));

        PaymentStatus newPaymentStatus = PaymentStatus.fromValue(snapshot.status());

        if (newPaymentStatus == PaymentStatus.UNKNOWN) {
            log.warn("Status desconhecido '{}' recebido para orderPaymentId={}, ignorando",
                    snapshot.status(), orderPaymentId);
            return;
        }

        if (payment.getPaymentStatus() == newPaymentStatus) {
            log.info("Pagamento {} ja esta no status {}, ignorando", orderPaymentId, newPaymentStatus);
            return;
        }

        Order order = payment.getOrder();

        if (order.getStatus().isTerminal()) {
            log.warn("Webhook recebido para pedido {} ja em status terminal {}, ignorando",
                    order.getId(), order.getStatus());
            return;
        }

        payment.registerTransaction(newPaymentStatus, snapshot.providerName(), snapshot.statusDetail(), LocalDateTime.now());
        
        paymentRepositoryJpa.save(payment);

        if (payment.getPaymentMethod() == PaymentType.PIX) {
            eventPublisher.publishEvent(new PixPaymentStatusUpdatedEvent(order.getId(), newPaymentStatus, snapshot.statusDetail()));
        }

        if (newPaymentStatus == PaymentStatus.APPROVED) {
            eventPublisher.publishEvent(new PaymentConfirmedEvent(payment.getId(), order.getId()));
        }

        log.info("Pagamento {} atualizado: paymentStatus={}", orderPaymentId, newPaymentStatus);
    }

}

