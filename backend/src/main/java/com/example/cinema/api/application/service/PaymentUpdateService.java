package com.example.cinema.api.application.service;

import com.example.cinema.api.application.exception.InvalidPaymentSnapshotException;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentUpdateService {

    private final OrderPaymentRepositoryJpa paymentRepositoryJpa;

    public PaymentUpdateService(OrderPaymentRepositoryJpa paymentRepositoryJpa) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
    }

    @Transactional
    public void processPaymentUpdate(ExternalPaymentSnapshot snapshot) {

        if (snapshot.externalReference() == null) {
            throw new InvalidPaymentSnapshotException("Pagamento sem external_reference");
        }

        Long orderId = snapshot.externalReference();

        OrderPayment payment = paymentRepositoryJpa.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Pagamento nao encontrado para orderId: " + orderId));

        PaymentStatus newPaymentStatus = PaymentStatus.fromValue(snapshot.status());

        if (newPaymentStatus == PaymentStatus.UNKNOWN) {
            log.warn("Status desconhecido '{}' recebido para orderId={}, ignorando",
                    snapshot.status(), orderId);
            return;
        }

        if (payment.getPaymentStatus() == newPaymentStatus) {
            log.info("Pagamento {} ja esta no status {}, ignorando", orderId, newPaymentStatus);
            return;
        }

        Order order = payment.getOrder();

        if (order.getStatus().isTerminal()) {
            log.warn("Webhook recebido para pedido {} ja em status terminal {}, ignorando",
                    orderId, order.getStatus());
            return;
        }

        OrderStatus newOrderStatus = newPaymentStatus.toOrderStatus()
                .orElseThrow(() -> new IllegalStateException(
                        "PaymentStatus sem mapeamento: " + newPaymentStatus));

        payment.updateStatus(newPaymentStatus, snapshot.statusDetail());
        order.moveToStatus(newOrderStatus);

        paymentRepositoryJpa.save(payment);

        log.info("Pedido {} atualizada: paymentStatus={} orderStatus={}",
                orderId, newPaymentStatus, newOrderStatus);
    }

}

