package com.example.cinema.api.domain.purchase.listeners;

import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailPurchaseNotificationListener {

    private final EmailService emailServicePort;

    public EmailPurchaseNotificationListener(EmailService emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCreatedEvent event) {
        emailServicePort.sendPurchaseNotificationEmail(event.getUser(), event.getPurchase());
    }

}
