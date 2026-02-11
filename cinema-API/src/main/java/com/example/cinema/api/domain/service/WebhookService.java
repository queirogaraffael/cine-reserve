package com.example.cinema.api.domain.service;

public interface WebhookService {
    void processWebhook(String payload);
}
