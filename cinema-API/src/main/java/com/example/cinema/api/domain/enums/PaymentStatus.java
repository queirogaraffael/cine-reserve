package com.example.cinema.api.domain.enums;

import lombok.Getter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum PaymentStatus {
    APPROVED("approved"),
    PENDING("pending"),
    IN_PROCESS("in_process"),
    AUTHORIZED("authorized"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    REFUNDED("refunded"),
    PARTIALLY_REFUNDED("partially_refunded"),
    CHARGED_BACK("charged_back"),
    EXPIRED("expired"),
    FAILED("failed"),
    UNKNOWN("unknown");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public static PaymentStatus fromValue(String value) {
        if (value == null) {
            log.warn("O gateway de pagamento enviou um status nulo.");
            return UNKNOWN;
        }

        for (PaymentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        log.error("Status de pagamento desconhecido recebido: {}", value);

        return UNKNOWN;
    }
}