package com.example.cinema.api.domain.purchase.listeners;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.services.EmailServicePort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailPurchaseNotificationListener {

    private final EmailServicePort emailServicePort;

    public EmailPurchaseNotificationListener(EmailServicePort emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @EventListener
    public void handlePurchase(PurchaseCreatedEvent event) {
        Purchase purchase = event.getPurchase();

        emailServicePort.sendPurchaseNotificationEmail(purchase);
    }
}
