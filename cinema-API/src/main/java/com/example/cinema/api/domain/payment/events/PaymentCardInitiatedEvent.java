package com.example.cinema.api.domain.payment.events;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentCardInitiatedEvent extends ApplicationEvent{

    private final User user;
    private final Purchase purchase;
    private final Payment payment;

    public PaymentCardInitiatedEvent(Object source, User user, Purchase purchase, Payment payment) {
        super(source);
        this.user = user;
        this.purchase = purchase;
        this.payment = payment;
    }
}