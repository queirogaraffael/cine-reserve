package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.email.EmailVerificationNotificationData;
import com.example.cinema.api.application.dto.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;

public interface EmailService {
    void sendWelcomeEmailComVerificacao(UserCreatedNotificationData dto);

    void sendEmailVerificacao(EmailVerificationNotificationData dto);

    void notifyPurchaseCreatedEmail(PurchaseCreatedNotificationData dto);

    void notifyPaymentCardInitiatedEmail(PaymentCardInitiatedNotificationData dto);
}