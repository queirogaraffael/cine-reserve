package com.example.cinema.api.domain.purchase.event;

import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PurchaseCreatedEvent extends ApplicationEvent {

    private final PurchaseCreatedNotificationData purchaseCreatedNotificationData;

    public PurchaseCreatedEvent(Object source, PurchaseCreatedNotificationData purchaseCreatedNotificationData) {
        super(source);
        this.purchaseCreatedNotificationData = purchaseCreatedNotificationData;
    }

}
