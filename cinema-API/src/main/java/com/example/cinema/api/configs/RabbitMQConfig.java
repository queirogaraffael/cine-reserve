package com.example.cinema.api.configs;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_WEBHOOK_EXCHANGE = "payment.webhook.exchange";

    public static final String PAYMENT_WEBHOOK_QUEUE = "payment.webhook.queue";
    public static final String PAYMENT_WEBHOOK_RETRY_QUEUE = "payment.webhook.retry.queue";
    public static final String PAYMENT_WEBHOOK_PARKING_LOT_QUEUE = "payment.webhook.parking-lot.queue";

    public static final String PAYMENT_WEBHOOK_ROUTING_KEY = "payment.webhook";
    public static final String PAYMENT_WEBHOOK_RETRY_ROUTING_KEY = "payment.webhook.retry";
    public static final String PAYMENT_WEBHOOK_PARKING_LOT_ROUTING_KEY = "payment.webhook.parking-lot";

    @Value("${rabbitmq.payment.webhook.retry.ttl.ms}")
    private int retryTtlMs;

    @Bean
    public DirectExchange paymentWebhookExchange() {
        return new DirectExchange(PAYMENT_WEBHOOK_EXCHANGE);
    }

    @Bean
    public Queue paymentWebhookQueue() {
        return QueueBuilder.durable(PAYMENT_WEBHOOK_QUEUE)
                .withArgument("x-dead-letter-exchange", PAYMENT_WEBHOOK_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAYMENT_WEBHOOK_RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding paymentWebhookBinding() {
        return BindingBuilder
                .bind(paymentWebhookQueue())
                .to(paymentWebhookExchange())
                .with(PAYMENT_WEBHOOK_ROUTING_KEY);
    }

    @Bean
    public Queue paymentWebhookRetryQueue() {
        return QueueBuilder.durable(PAYMENT_WEBHOOK_RETRY_QUEUE)
                .withArgument("x-message-ttl", retryTtlMs)
                .withArgument("x-dead-letter-exchange", PAYMENT_WEBHOOK_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAYMENT_WEBHOOK_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding paymentWebhookRetryBinding() {
        return BindingBuilder
                .bind(paymentWebhookRetryQueue())
                .to(paymentWebhookExchange())
                .with(PAYMENT_WEBHOOK_RETRY_ROUTING_KEY);
    }

    @Bean
    public Queue paymentWebhookParkingLotQueue() {
        return QueueBuilder.durable(PAYMENT_WEBHOOK_PARKING_LOT_QUEUE).build();
    }

    @Bean
    public Binding paymentWebhookParkingLotBinding() {
        return BindingBuilder
                .bind(paymentWebhookParkingLotQueue())
                .to(paymentWebhookExchange())
                .with(PAYMENT_WEBHOOK_PARKING_LOT_ROUTING_KEY);
    }
}

