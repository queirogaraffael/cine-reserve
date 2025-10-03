package com.example.cinema.api.domain.user.listeners;

import com.example.cinema.api.infrastructure.email.EmailServiceAdapter;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailUserWelcomeListener {

    private final EmailServiceAdapter emailServiceAdapter;

    public EmailUserWelcomeListener(EmailServiceAdapter emailServiceAdapter) {
        this.emailServiceAdapter = emailServiceAdapter;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        emailServiceAdapter.sendWelcomeEmail(event.getEmail(), event.getName());

    }
}
