package com.example.cinema.api.domain.purchase.event;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PurchaseCreatedEvent extends ApplicationEvent {

    private final Purchase purchase;
    private final User user;

    public PurchaseCreatedEvent(Object source, User user, Purchase purchase) {
        super(source);
        this.user = user;
        this.purchase = purchase;
    }

}
