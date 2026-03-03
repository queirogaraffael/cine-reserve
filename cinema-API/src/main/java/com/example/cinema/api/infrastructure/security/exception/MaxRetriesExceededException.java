package com.example.cinema.api.infrastructure.security.exception;

import lombok.Getter;

@Getter
public class MaxRetriesExceededException extends RuntimeException {

    private final Long paymentId;

    public MaxRetriesExceededException(Long paymentId) {
        super("Max retries exceeded for payment " + paymentId);
        this.paymentId = paymentId;
    }

}
