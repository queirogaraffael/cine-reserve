package com.example.cinema.api.domain.purchase;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Getter
public enum PurchaseStatus {

    CREATED("created"),
    WAITING_PAYMENT("waiting_payment"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled");

    private final String value;
    private Set<PurchaseStatus> allowedTransitions;

    PurchaseStatus(String value) {
        this.value = value;
        this.allowedTransitions = Collections.emptySet();
    }

    static {

        CREATED.allowedTransitions = of(WAITING_PAYMENT, CANCELLED, EXPIRED);

        WAITING_PAYMENT.allowedTransitions = of(CONFIRMED, CANCELLED, EXPIRED);

        CONFIRMED.allowedTransitions = of(REFUNDED);

        REFUNDED.allowedTransitions = Collections.emptySet();

        CANCELLED.allowedTransitions = Collections.emptySet();

        EXPIRED.allowedTransitions = Collections.emptySet();
    }

    private static Set<PurchaseStatus> of(PurchaseStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(EnumSet.of(statuses[0], statuses));
    }

    public boolean canTransitionTo(PurchaseStatus next) {
        return next != null && allowedTransitions.contains(next);
    }

    public PurchaseStatus transitionTo(PurchaseStatus next) {
        if (!canTransitionTo(next)) {
            log.error("Transição inválida: {} -> {}", this, next);
            throw new IllegalStateException("Transição inválida: " + this + " -> " + next);
        }
        return next;
    }

    public static PurchaseStatus fromValue(String value) {
        if (value == null) return null;
        for (PurchaseStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) return status;
        }
        return null;
    }
}