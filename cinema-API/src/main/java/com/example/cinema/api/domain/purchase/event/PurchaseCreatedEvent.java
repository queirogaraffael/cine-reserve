package com.example.cinema.api.domain.purchase.event;

import com.example.cinema.api.domain.entities.Purchase;
import org.springframework.context.ApplicationEvent;

public class PurchaseCreatedEvent extends ApplicationEvent {

    private final Purchase purchase;

    public PurchaseCreatedEvent(Object source, Purchase purchase) {
        super(source);
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
