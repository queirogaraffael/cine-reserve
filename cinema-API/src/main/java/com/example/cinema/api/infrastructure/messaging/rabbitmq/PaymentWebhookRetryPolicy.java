package com.example.cinema.api.infrastructure.messaging.rabbitmq;

import com.example.cinema.api.infrastructure.messaging.config.RabbitMQPaymentWebhookConfig;
import com.example.cinema.api.shared.dtos.webhook.PaymentWebhookEvent;
import com.example.cinema.api.shared.exceptions.MaxRetriesExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;


@Component
@Slf4j
public class PaymentWebhookRetryPolicy {

    public static final int MAX_RETRIES = 4;
    public static final String RETRY_HEADER = "x-retry-count";

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public PaymentWebhookRetryPolicy(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void retry(PaymentWebhookEvent event, Message message, int retries, String reason) {

        if (retries >= MAX_RETRIES) {
            throw new MaxRetriesExceededException(event.getPaymentId());
        }

        String routingKey = chooseRetryRoutingKey(retries + 1);

        MessageProperties props = message.getMessageProperties();
        props.getHeaders().put(RETRY_HEADER, retries + 1);

        Message retryMessage = new Message(message.getBody(), props);

        rabbitTemplate.send(
                RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_EXCHANGE,
                routingKey,
                retryMessage
        );

        log.warn("Retry {} scheduled for payment {} ({}) via {}",
                retries + 1, event.getPaymentId(), reason, routingKey);
    }

    public int getRetryCount(Message message) {
        Object value = message.getMessageProperties()
                .getHeaders()
                .get(RETRY_HEADER);

        return value instanceof Integer ? (Integer) value : 0;
    }

    private String chooseRetryRoutingKey(int attempt) {
        boolean jitter = random.nextBoolean();

        return switch (attempt) {
            case 1 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_1A : RabbitMQPaymentWebhookConfig.RETRY_1B;
            case 2 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_2A : RabbitMQPaymentWebhookConfig.RETRY_2B;
            case 3 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_3A : RabbitMQPaymentWebhookConfig.RETRY_3B;
            case 4 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_4A : RabbitMQPaymentWebhookConfig.RETRY_4B;
            default -> RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_PARKING_LOT_QUEUE;
        };
    }
}

