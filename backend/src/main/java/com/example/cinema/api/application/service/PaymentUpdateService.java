package com.example.cinema.api.application.service;

import com.example.cinema.api.application.exception.InvalidPaymentSnapshotException;
import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.purchase.PurchaseStatus;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentUpdateService {

    private final PaymentRepositoryJpa paymentRepositoryJpa;

    public PaymentUpdateService(PaymentRepositoryJpa paymentRepositoryJpa) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
    }

    @Transactional
    public void processPaymentUpdate(ExternalPaymentSnapshot snapshot) {

        if (snapshot.externalReference() == null) {
            throw new InvalidPaymentSnapshotException("Pagamento sem external_reference");
        }

        Long purchaseId = snapshot.externalReference();

        Payment payment = paymentRepositoryJpa.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Pagamento nao encontrado para purchaseId: " + purchaseId));

        PaymentStatus newPaymentStatus = PaymentStatus.fromValue(snapshot.status());

        if (newPaymentStatus == PaymentStatus.UNKNOWN) {
            log.warn("Status desconhecido '{}' recebido para purchaseId={}, ignorando",
                    snapshot.status(), purchaseId);
            return;
        }

        if (payment.getPaymentStatus() == newPaymentStatus) {
            log.info("Pagamento {} ja esta no status {}, ignorando", purchaseId, newPaymentStatus);
            return;
        }

        Purchase purchase = payment.getPurchase();

        if (purchase.getPurchaseStatus().isTerminal()) {
            log.warn("Webhook recebido para compra {} ja em status terminal {}, ignorando",
                    purchaseId, purchase.getPurchaseStatus());
            return;
        }

        PurchaseStatus newPurchaseStatus = newPaymentStatus.toPurchaseStatus()
                .orElseThrow(() -> new IllegalStateException(
                        "PaymentStatus sem mapeamento: " + newPaymentStatus));

        payment.updateStatus(newPaymentStatus, snapshot.statusDetail());
        purchase.moveToStatus(newPurchaseStatus);

        paymentRepositoryJpa.save(payment);

        log.info("Compra {} atualizada: paymentStatus={} purchaseStatus={}",
                purchaseId, newPaymentStatus, newPurchaseStatus);
    }

}

