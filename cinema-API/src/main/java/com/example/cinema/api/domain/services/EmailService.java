package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;

public interface EmailService {
    void sendWelcomeEmail(String emailFromUser, String userName);

    void notifyPurchaseCreated(User user, Purchase purchase);

    void notifyPaymentCardInitiated(User user, Purchase purchase, Payment payment);
}