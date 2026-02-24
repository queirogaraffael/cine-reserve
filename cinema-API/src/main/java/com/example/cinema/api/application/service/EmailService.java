package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.application.dto.email.WelcomeNotificationData;

public interface EmailService {
    void sendWelcomeEmail(WelcomeNotificationData dto);

    void notifyPurchaseCreatedEmail(PurchaseCreatedNotificationData dto);

    void notifyPaymentCardInitiatedEmail(PaymentCardInitiatedNotificationData dto);
}