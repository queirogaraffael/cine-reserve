package com.example.cinema.api.domain.purchase.listeners;

import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailPurchaseNotificationListener {

    private final EmailService emailServicePort;

    public EmailPurchaseNotificationListener(EmailService emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCreatedEvent event) {

        try {
            emailServicePort.sendPurchaseNotificationEmail(event.getUser(), event.getPurchase());
        } catch (Exception e) {
            log.error("Erro ao enviar email da compra {}", event.getPurchase().getId(), e);
        }

    }

}
