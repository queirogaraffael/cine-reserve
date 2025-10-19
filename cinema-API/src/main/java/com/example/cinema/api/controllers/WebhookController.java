package com.example.cinema.api.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface WebhookController {
    @PostMapping
    void receiveNotification(@RequestBody String notification);
}
