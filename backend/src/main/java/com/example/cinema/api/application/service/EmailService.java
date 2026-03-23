package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;

public interface EmailService {
    void sendWelcomeEmail(UserCreatedNotificationData dto);

    void notifyPurchaseCreatedEmail(PurchaseCreatedNotificationData dto);

    void notifyPaymentCardInitiatedEmail(PaymentCardInitiatedNotificationData dto);
}