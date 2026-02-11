package com.example.cinema.api.domain.service;

import com.example.cinema.api.shared.dtos.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.shared.dtos.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.shared.dtos.email.WelcomeNotificationData;

public interface EmailService {
    void sendWelcomeEmail(WelcomeNotificationData dto);

    void notifyPurchaseCreatedEmail(PurchaseCreatedNotificationData dto);

    void notifyPaymentCardInitiatedEmail(PaymentCardInitiatedNotificationData dto);
}