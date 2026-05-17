package com.example.cinema.api.controller;


import com.example.cinema.api.application.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookPagamentoController {

    WebhookService webhookService;

    public WebhookPagamentoController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> receiveNotification(
            @RequestHeader("x-signature") String signature,
            @RequestHeader("x-request-id") String requestId,
            @RequestBody String notificationJson) {

        webhookService.processWebhook(signature, requestId, notificationJson);
        return ResponseEntity.ok().build();
    }
}
