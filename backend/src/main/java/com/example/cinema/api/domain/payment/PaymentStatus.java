package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.purchase.PurchaseStatus;
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
        return Collections.unmodifiableSet(EnumSet.of(statuses[0], statuses));
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

    private static final Map<PaymentStatus, PurchaseStatus> PURCHASE_STATUS_MAP =
            Map.ofEntries(
                    Map.entry(PENDING, PurchaseStatus.WAITING_PAYMENT),
                    Map.entry(IN_PROCESS, PurchaseStatus.WAITING_PAYMENT),
                    Map.entry(AUTHORIZED, PurchaseStatus.WAITING_PAYMENT),
                    Map.entry(APPROVED, PurchaseStatus.CONFIRMED),
                    Map.entry(REJECTED, PurchaseStatus.CANCELLED),
                    Map.entry(CANCELLED, PurchaseStatus.CANCELLED),
                    Map.entry(EXPIRED, PurchaseStatus.CANCELLED),
                    Map.entry(FAILED, PurchaseStatus.CANCELLED),
                    Map.entry(PARTIALLY_REFUNDED, PurchaseStatus.CANCELLED),
                    Map.entry(REFUNDED, PurchaseStatus.CANCELLED),
                    Map.entry(CHARGED_BACK, PurchaseStatus.CANCELLED)
            );

    public Optional<PurchaseStatus> toPurchaseStatus() {
        return Optional.ofNullable(PURCHASE_STATUS_MAP.get(this));
    }
}

