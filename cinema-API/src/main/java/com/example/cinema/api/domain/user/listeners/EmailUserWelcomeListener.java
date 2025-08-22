package com.example.cinema.api.domain.user.listeners;

import com.example.cinema.api.domain.services.EmailService;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailUserWelcomeListener {

    private final EmailService emailService;

    public EmailUserWelcomeListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        emailService.sendWelcomeEmail(event.getEmail(), event.getName());

    }
}
