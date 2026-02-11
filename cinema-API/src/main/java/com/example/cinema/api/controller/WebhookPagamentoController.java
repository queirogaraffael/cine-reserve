package com.example.cinema.api.controller;


import com.example.cinema.api.application.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookPagamentoController {

    WebhookService webhookService;

    public WebhookPagamentoController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(summary = "Recebe notificações de webhooks de Pagemento")
    @PostMapping("/payment")
    public ResponseEntity<Void> receiveNotification(@RequestBody String notificationJson) {
        webhookService.processWebhook(notificationJson);

        return ResponseEntity.ok().build();
    }
}
