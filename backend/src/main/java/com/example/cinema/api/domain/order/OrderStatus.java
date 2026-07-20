package com.example.cinema.api.domain.order;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Getter
public enum OrderStatus {

    CREATED("created"),
    WAITING_PAYMENT("waiting_payment"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled");

    private final String value;
    private Set<OrderStatus> allowedTransitions;

    OrderStatus(String value) {
        this.value = value;
        this.allowedTransitions = Collections.emptySet();
    }

    static {
        CREATED.allowedTransitions         = of(WAITING_PAYMENT, CANCELLED);
        WAITING_PAYMENT.allowedTransitions  = of(CONFIRMED, CANCELLED);
        CONFIRMED.allowedTransitions        = Collections.emptySet();
        CANCELLED.allowedTransitions        = Collections.emptySet();
    }

    private static Set<OrderStatus> of(OrderStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(statuses)));
    }

    public boolean canTransitionTo(OrderStatus next) {
        return next != null && allowedTransitions.contains(next);
    }

    public boolean isTerminal() {
        return this == CONFIRMED || this == CANCELLED;
    }

    public OrderStatus transitionTo(OrderStatus next) {
        if (!canTransitionTo(next)) {
            log.error("Transição inválida: {} -> {}", this, next);
            throw new IllegalStateException("Transição inválida: " + this + " -> " + next);
        }
        return next;
    }

    public static OrderStatus fromValue(String value) {
        if (value == null) return null;
        for (OrderStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) return status;
        }
        return null;
    }
}
