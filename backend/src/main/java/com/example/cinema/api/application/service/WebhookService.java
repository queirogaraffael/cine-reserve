package com.example.cinema.api.application.service;

public interface WebhookService {
    void processWebhook(String signature, String requestId, String notificationJson);

}
