package com.example.cinema.api.domain.enums;

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
    FAILED("failed");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + value);
    }
}
