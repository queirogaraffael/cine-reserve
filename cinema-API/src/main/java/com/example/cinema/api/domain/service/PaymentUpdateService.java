package com.example.cinema.api.domain.service;

import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.shared.dtos.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.shared.dtos.webhook.PaymentWebhookEvent;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentUpdateService {

    private final PaymentRepositoryJpa paymentRepositoryJpa;

    public PaymentUpdateService(PaymentRepositoryJpa paymentRepositoryJpa) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
    }

    public void processPaymentUpdate(ExternalPaymentSnapshot externalPaymentSnapshot, PaymentWebhookEvent event) {

        if (externalPaymentSnapshot.getExternalReference() == null) {
            throw new IllegalStateException("Pagamento sem external_reference");
        }

        Long purchaseId = externalPaymentSnapshot.getExternalReference();

        var paymentLocal = paymentRepositoryJpa.findByPurchaseId(purchaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pagamento não encontrado para o PurchaseId: " + purchaseId)
                );

        if (paymentLocal.getVersion() > event.getVersion()) {
            log.info("Evento desatualizado ignorado para o pagamento {}", purchaseId);
            return;
        }

        PaymentStatus newStatus = PaymentStatus.fromValue(externalPaymentSnapshot.getStatus());

        if (paymentLocal.getPaymentStatus() == newStatus) {
            log.info("Pagamento {} já está no status {}", purchaseId, newStatus);
            return;
        }

        paymentLocal.setPaymentStatus(paymentLocal.getPaymentStatus().transitionTo(newStatus));

        paymentLocal.setStatusDetail(externalPaymentSnapshot.getStatusDetail());

        paymentRepositoryJpa.save(paymentLocal);
    }
}


