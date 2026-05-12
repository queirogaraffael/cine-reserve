package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.application.dto.webhook.PaymentWebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentUpdateService {

    private final PaymentRepositoryJpa paymentRepositoryJpa;

    public PaymentUpdateService(PaymentRepositoryJpa paymentRepositoryJpa) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
    }

    @Transactional
    public void processPaymentUpdate(ExternalPaymentSnapshot externalPaymentSnapshot, PaymentWebhookEvent event) {

        Long purchaseId = externalPaymentSnapshot.externalReference();

        var paymentLocal = paymentRepositoryJpa.findByPurchaseId(purchaseId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Pagamento não encontrado para o PurchaseId: " + purchaseId));

        if (paymentLocal.isOutdatedVersion(event.getVersion())) {
            log.info("Evento desatualizado ignorado para o pagamento {}", purchaseId);
            return;
        }

        paymentLocal.updateVersion(event.getVersion());

        PaymentStatus novoStatus = PaymentStatus.fromValue(externalPaymentSnapshot.status());

        if (paymentLocal.getPaymentStatus() == novoStatus) {
            log.info("Pagamento {} já está no status {}", purchaseId, novoStatus);
            return;
        }

        paymentLocal.updateStatus(novoStatus, externalPaymentSnapshot.statusDetail());

        paymentRepositoryJpa.save(paymentLocal);
    }
}

