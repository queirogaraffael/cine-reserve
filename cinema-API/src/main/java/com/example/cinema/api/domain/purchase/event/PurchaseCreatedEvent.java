package com.example.cinema.api.domain.purchase.event;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import org.springframework.context.ApplicationEvent;

public class PurchaseCreatedEvent extends ApplicationEvent {

    private final Purchase purchase;
    private final User user;

    public PurchaseCreatedEvent(Object source, User user, Purchase purchase) {
        super(source);
        this.user = user;
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public User getUser() {
        return user;
    }
}
