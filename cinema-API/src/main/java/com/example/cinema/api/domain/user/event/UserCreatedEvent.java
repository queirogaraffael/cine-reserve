package com.example.cinema.api.domain.user.event;

import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {

    private final String email;
    private final String name;

    public UserCreatedEvent(Object source, String email, String name) {
        super(source);
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
