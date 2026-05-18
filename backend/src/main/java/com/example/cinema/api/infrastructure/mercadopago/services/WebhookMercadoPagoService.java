package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.infrastructure.messaging.rabbitmq.config.RabbitMQPaymentWebhookConfig;
import com.example.cinema.api.application.service.WebhookService;
import com.example.cinema.api.infrastructure.mercadopago.dtos.MercadoPagoWebhookDTO;
import com.example.cinema.api.application.dto.webhook.PaymentWebhookEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Slf4j
public class WebhookMercadoPagoService implements WebhookService {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    public WebhookMercadoPagoService(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void processWebhook(String payload) {

        MercadoPagoWebhookDTO webhook;

        try {
            webhook = objectMapper.readValue(payload, MercadoPagoWebhookDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Payload invalido: {}", payload, e);
            return;
        }

        if (!"payment".equals(webhook.getType())) {
            log.info("Webhook ignorado: {}", webhook.getType());
            return;
        }

        if (webhook.getData() == null || webhook.getData().getId() == null) {
            log.error("Webhook payment sem data/id valido: {}", payload);
            return;
        }

        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.setPaymentId(webhook.getData().getId());
        event.setRawPayload(payload);
        event.setReceivedAt(OffsetDateTime.now());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_EXCHANGE,
                    RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_QUEUE,
                    event
            );
            log.info("Evento {} enviado para processamento", event.getPaymentId());
        } catch (AmqpException e) {
            log.error("Erro ao publicar evento no RabbitMQ. PaymentId={}",
                    event.getPaymentId(), e);
        }
    }


}
