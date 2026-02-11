package com.example.cinema.api.infrastructure.messaging.rabbitmq;

import com.example.cinema.api.infrastructure.messaging.rabbitmq.config.RabbitMQPaymentWebhookConfig;
import com.example.cinema.api.shared.dtos.webhook.PaymentWebhookEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureHandler {

    private final RabbitTemplate rabbitTemplate;

    public PaymentFailureHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToParkingLot(PaymentWebhookEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_EXCHANGE,
                RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_PARKING_LOT_QUEUE,
                event
        );
    }
}
