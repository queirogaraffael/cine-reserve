package com.example.cinema.api.domain.purchase.listeners;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.infrastructure.email.EmailServiceImpl;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailPurchaseNotificationListener {

    private final EmailServiceImpl emailServiceImpl;

    public EmailPurchaseNotificationListener(EmailServiceImpl emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
    }

    @EventListener
    public void handlePurchase(PurchaseCreatedEvent event) {
        Purchase purchase = event.getPurchase();

        emailServiceImpl.sendPurchaseNotificationEmail(purchase.getUser().getEmail(), purchase);
    }
}
