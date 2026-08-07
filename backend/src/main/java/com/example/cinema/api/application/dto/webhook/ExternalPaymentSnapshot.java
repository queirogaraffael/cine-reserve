package com.example.cinema.api.application.dto.webhook;


public record ExternalPaymentSnapshot(Long externalReference, String status, String statusDetail, String providerName) {
}
