package com.example.cinema.api.controller;


import com.example.cinema.api.infrastructure.mercadopago.MercadoPagoWebhookValidator;
import com.example.cinema.api.application.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Webhooks")
@RestController
@RequestMapping("/api/webhooks")
@Slf4j
public class PaymentWebhookController {

    private final WebhookService webhookService;
    private final MercadoPagoWebhookValidator hmacValidator;


    public PaymentWebhookController(WebhookService webhookService, MercadoPagoWebhookValidator hmacValidator) {
        this.webhookService = webhookService;
        this.hmacValidator = hmacValidator;
    }

    @Operation(summary = "Receber notificação de pagamento (Mercado Pago)",
            description = "Recebe e processa webhooks do Mercado Pago. Valida a assinatura (HMAC-SHA256) usando o header x-signature e o requestId para garantir a autenticidade.")
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
