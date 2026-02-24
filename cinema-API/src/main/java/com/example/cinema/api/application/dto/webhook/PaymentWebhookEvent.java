package com.example.cinema.api.application.dto.webhook;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PaymentWebhookEvent {

    private Long paymentId;
    private int version;
    private String rawPayload;
    private OffsetDateTime receivedAt;

}
