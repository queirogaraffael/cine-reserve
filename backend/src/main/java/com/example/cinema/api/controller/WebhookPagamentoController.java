package com.example.cinema.api.controller;


import com.example.cinema.api.infrastructure.mercadopago.MercadoPagoWebhookValidator;
import com.example.cinema.api.application.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
public class WebhookPagamentoController {

    private final WebhookService webhookService;
    private final MercadoPagoWebhookValidator hmacValidator;


    public WebhookPagamentoController(WebhookService webhookService, MercadoPagoWebhookValidator hmacValidator) {
        this.webhookService = webhookService;
        this.hmacValidator = hmacValidator;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> receiveNotification(@RequestHeader("x-signature") String signature,
                                                    @RequestHeader("x-request-id") String requestId,
                                                    @RequestBody String notificationJson) {

        if (!hmacValidator.isValid(signature, requestId, notificationJson)) {
            log.warn("Webhook com assinatura invalida rejeitado. requestId={}", requestId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        webhookService.processWebhook(notificationJson);
        return ResponseEntity.ok().build();
    }
}
