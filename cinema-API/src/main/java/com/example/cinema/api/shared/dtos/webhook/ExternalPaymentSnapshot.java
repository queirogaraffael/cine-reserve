package com.example.cinema.api.shared.dtos.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalPaymentSnapshot {
    private final Long externalReference;
    private final String status;
    private final String statusDetail;
}
