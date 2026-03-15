package com.example.cinema.api;

import lombok.Getter;

@Getter
public class MaxRetriesExceededException extends RuntimeException {

    private final Long paymentId;

    public MaxRetriesExceededException(Long paymentId) {
        super("Max retries exceeded for payment " + paymentId);
        this.paymentId = paymentId;
    }

}
