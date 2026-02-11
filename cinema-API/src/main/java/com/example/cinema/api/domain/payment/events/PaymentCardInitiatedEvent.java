package com.example.cinema.api.domain.payment.events;

import com.example.cinema.api.shared.dtos.email.PaymentCardInitiatedNotificationData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentCardInitiatedEvent extends ApplicationEvent {

    private final PaymentCardInitiatedNotificationData paymentCardInitiatedNotificationData;

    public PaymentCardInitiatedEvent(Object source,
                                     PaymentCardInitiatedNotificationData paymentCardInitiatedNotificationData) {
        super(source);
        this.paymentCardInitiatedNotificationData = paymentCardInitiatedNotificationData;
    }
}
