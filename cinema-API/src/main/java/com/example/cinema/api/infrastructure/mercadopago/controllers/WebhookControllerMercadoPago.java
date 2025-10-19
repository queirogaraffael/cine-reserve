package com.example.cinema.api.infrastructure.mercadopago.controllers;


import com.example.cinema.api.controllers.WebhookController;
import com.example.cinema.api.domain.services.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class WebhookControllerMercadoPago implements WebhookController {

    WebhookService webhookService;

    public WebhookControllerMercadoPago(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(summary = "Recebe notificações de webhooks do Mercado Pago")
    @PostMapping
    @Override
    public void receiveNotification(@RequestBody String notificationJson) {
        webhookService.processWebhook(notificationJson);
    }
}
