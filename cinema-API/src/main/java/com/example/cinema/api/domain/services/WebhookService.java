package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;

public interface WebhookService {
    void processWebhook(String payload);
}
