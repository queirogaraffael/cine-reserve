package com.example.cinema.api.domain.enums;

import lombok.Getter;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

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
    private Set<PaymentStatus> allowedTransitions =
            EnumSet.noneOf(PaymentStatus.class);

    PaymentStatus(String value) {
        this.value = value;
    }

    static {
        PENDING.allowedTransitions = unmodifiable(
                IN_PROCESS, AUTHORIZED, APPROVED, REJECTED, CANCELLED, EXPIRED
        );

        IN_PROCESS.allowedTransitions = unmodifiable(
                APPROVED, REJECTED, CANCELLED
        );

        AUTHORIZED.allowedTransitions = unmodifiable(
                APPROVED, CANCELLED, EXPIRED
        );

        APPROVED.allowedTransitions = unmodifiable(
                REFUNDED, PARTIALLY_REFUNDED, CHARGED_BACK
        );

        PARTIALLY_REFUNDED.allowedTransitions = unmodifiable(
                REFUNDED
        );

        REFUNDED.allowedTransitions = empty();
        REJECTED.allowedTransitions = empty();
        CANCELLED.allowedTransitions = empty();
        CHARGED_BACK.allowedTransitions = empty();
        EXPIRED.allowedTransitions = empty();

        FAILED.allowedTransitions = unmodifiable(
                PENDING, IN_PROCESS
        );

        UNKNOWN.allowedTransitions = unmodifiable(
                PENDING, IN_PROCESS, AUTHORIZED, APPROVED
        );
    }

    private static Set<PaymentStatus> unmodifiable(PaymentStatus... statuses) {
        return Collections.unmodifiableSet(EnumSet.of(statuses[0], statuses));
    }

    private static Set<PaymentStatus> empty() {
        return Collections.unmodifiableSet(EnumSet.noneOf(PaymentStatus.class));
    }

    public boolean canTransitionTo(PaymentStatus next) {
        return next != null && allowedTransitions.contains(next);
    }

    public PaymentStatus transitionTo(PaymentStatus next) {
        if (!canTransitionTo(next)) {
            log.error("Transição inválida de pagamento. Estado atual={}, próximo={}", this, next);
            throw new IllegalStateException("Transição inválida: " + this + " -> " + next);
        }
        return next;
    }

    public static PaymentStatus fromValue(String value) {
        if (value == null) {
            log.warn("O gateway de pagamento enviou um status nulo.");
            return UNKNOWN;
        }

        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        log.error("Status de pagamento desconhecido recebido: {}", value);
        return UNKNOWN;
    }
}

