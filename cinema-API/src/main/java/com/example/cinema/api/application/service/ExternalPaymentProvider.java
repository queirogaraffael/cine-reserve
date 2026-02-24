package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;

public interface ExternalPaymentProvider {

    ExternalPaymentSnapshot getPayment(Long paymentId);

}
