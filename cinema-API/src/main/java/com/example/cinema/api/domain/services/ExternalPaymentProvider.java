package com.example.cinema.api.domain.services;

import com.example.cinema.api.shared.dtos.webhook.ExternalPaymentSnapshot;

public interface ExternalPaymentProvider {

    ExternalPaymentSnapshot getPayment(Long paymentId);

}
