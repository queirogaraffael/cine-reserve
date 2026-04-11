package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.purchase.PurchaseStatus;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.user.User;

import java.math.BigDecimal;
import java.util.UUID;

public class PurchaseFixture {

    public static Purchase valid() {
        User user = UserFixture.valid();
        String idempotencyKey = UUID.randomUUID().toString();
        return new Purchase(user, idempotencyKey);
    }

    public static Purchase withStatus(PurchaseStatus status) {
        Purchase purchase = valid();
        purchase.moveToStatus(status);
        return purchase;
    }

    public static Purchase withTicket(SeatReservation reservation, TicketCategory category, BigDecimal price) {
        Purchase purchase = valid();
        purchase.addTicket(reservation, category, price);
        return purchase;
    }

    public static Purchase withPayment(Payment payment) {
        Purchase purchase = valid();
        purchase.attachPayment(payment);
        return purchase;
    }
}