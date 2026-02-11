package com.example.cinema.api.domain.user.event;

import com.example.cinema.api.shared.dtos.email.WelcomeNotificationData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {

    private final WelcomeNotificationData welcomeNotificationData;

    public UserCreatedEvent(Object source, WelcomeNotificationData welcomeNotificationData) {
        super(source);
        this.welcomeNotificationData = welcomeNotificationData;
    }
}
