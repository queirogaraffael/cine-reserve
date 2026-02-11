package com.example.cinema.api.infrastructure.email.listener;

import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailUserWelcomeListener {

    private final EmailService emailServicePort;

    public EmailUserWelcomeListener(EmailService emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        emailServicePort.sendWelcomeEmail(event.getWelcomeNotificationData());

    }
}
