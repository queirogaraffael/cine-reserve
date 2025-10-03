package com.example.cinema.api.domain.purchase.listeners;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.services.EmailServicePort;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailPurchaseNotificationListener {

    private final EmailServicePort emailServicePort;

    public EmailPurchaseNotificationListener(EmailServicePort emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCreatedEvent event) {
        emailServicePort.sendPurchaseNotificationEmail(event.getPurchase());
    }

}
