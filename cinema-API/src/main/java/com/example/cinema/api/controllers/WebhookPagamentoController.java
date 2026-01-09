package com.example.cinema.api.controllers;


import com.example.cinema.api.domain.services.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class WebhookPagamentoController {

    WebhookService webhookService;

    public WebhookPagamentoController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(summary = "Recebe notificações de webhooks de Pagemento")
    @PostMapping
    public ResponseEntity<Void> receiveNotification(@RequestBody String notificationJson) {
        webhookService.processWebhook(notificationJson);

        return ResponseEntity.ok().build();
    }
}
