package com.example.cinema.api.domain.services;

public interface WebhookService {
    void processWebhook(String payload);
}
