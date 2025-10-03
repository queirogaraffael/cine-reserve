package com.example.cinema.api.domain.user.listeners;

import com.example.cinema.api.infrastructure.email.EmailServiceImpl;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailUserWelcomeListener {

    private final EmailServiceImpl emailServiceImpl;

    public EmailUserWelcomeListener(EmailServiceImpl emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        emailServiceImpl.sendWelcomeEmail(event.getEmail(), event.getName());

    }
}
