package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Purchase;

public interface EmailServicePort {
    void sendWelcomeEmail(String emailFromUser, String userName);
    void sendPurchaseNotificationEmail(Purchase purchase);
}
