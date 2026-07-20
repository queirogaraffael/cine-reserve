package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.order.OrderStatus;
import lombok.Getter;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
public enum PaymentStatus {

    PENDING("pending"),
    IN_PROCESS("in_process"),
    AUTHORIZED("authorized"),
    APPROVED("approved"),
    PARTIALLY_REFUNDED("partially_refunded"),
    REFUNDED("refunded"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    CHARGED_BACK("charged_back"),
    EXPIRED("expired"),
    FAILED("failed"),
    UNKNOWN("unknown");

    private final String value;
    private Set<PaymentStatus> allowedTransitions;

    PaymentStatus(String value) {
        this.value = value;
        this.allowedTransitions = Collections.emptySet();
    }

    static {
        PENDING.allowedTransitions = of(IN_PROCESS, AUTHORIZED, APPROVED, REJECTED, CANCELLED, EXPIRED);
        IN_PROCESS.allowedTransitions = of(APPROVED, REJECTED, CANCELLED);
        AUTHORIZED.allowedTransitions = of(APPROVED, CANCELLED, EXPIRED);
        APPROVED.allowedTransitions = of(REFUNDED, PARTIALLY_REFUNDED, CHARGED_BACK);
        PARTIALLY_REFUNDED.allowedTransitions = of(REFUNDED);

        FAILED.allowedTransitions = of(PENDING, IN_PROCESS);
    }

    private static Set<PaymentStatus> of(PaymentStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(statuses)));
    }

    public boolean canTransitionTo(PaymentStatus next) {
        return next != null && allowedTransitions.contains(next);
    }

    public PaymentStatus transitionTo(PaymentStatus next) {
        if (!canTransitionTo(next)) {
            log.error("Transição inválida: {} -> {}", this, next);
            throw new IllegalStateException("Transição inválida: " + this + " -> " + next);
        }
        return next;
    }

    public static PaymentStatus fromValue(String value) {
        if (value == null) return UNKNOWN;
        for (PaymentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) return status;
        }
        return UNKNOWN;
    }

    private static final Map<PaymentStatus, OrderStatus> ORDER_STATUS_MAP =
            Map.ofEntries(
                    Map.entry(PENDING, OrderStatus.WAITING_PAYMENT),
                    Map.entry(IN_PROCESS, OrderStatus.WAITING_PAYMENT),
                    Map.entry(AUTHORIZED, OrderStatus.WAITING_PAYMENT),
                    Map.entry(APPROVED, OrderStatus.CONFIRMED),
                    Map.entry(REJECTED, OrderStatus.CANCELLED),
                    Map.entry(CANCELLED, OrderStatus.CANCELLED),
                    Map.entry(EXPIRED, OrderStatus.CANCELLED),
                    Map.entry(FAILED, OrderStatus.CANCELLED),
                    Map.entry(PARTIALLY_REFUNDED, OrderStatus.CANCELLED),
                    Map.entry(REFUNDED, OrderStatus.CANCELLED),
                    Map.entry(CHARGED_BACK, OrderStatus.CANCELLED)
            );

    public Optional<OrderStatus> toOrderStatus() {
        return Optional.ofNullable(ORDER_STATUS_MAP.get(this));
    }
}

