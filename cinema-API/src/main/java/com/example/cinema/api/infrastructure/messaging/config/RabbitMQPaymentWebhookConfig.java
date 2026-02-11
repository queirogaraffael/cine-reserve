package com.example.cinema.api.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQPaymentWebhookConfig {

    public static final String PAYMENT_WEBHOOK_EXCHANGE = "payment.webhook.exchange";

    public static final String PAYMENT_WEBHOOK_QUEUE = "payment.webhook.queue";
    public static final String PAYMENT_WEBHOOK_PARKING_LOT_QUEUE = "payment.webhook.parking-lot.queue";

    public static final String RETRY_1A = "payment.webhook.retry.1a";
    public static final String RETRY_1B = "payment.webhook.retry.1b";
    public static final String RETRY_2A = "payment.webhook.retry.2a";
    public static final String RETRY_2B = "payment.webhook.retry.2b";
    public static final String RETRY_3A = "payment.webhook.retry.3a";
    public static final String RETRY_3B = "payment.webhook.retry.3b";
    public static final String RETRY_4A = "payment.webhook.retry.4a";
    public static final String RETRY_4B = "payment.webhook.retry.4b";

    private Binding retryBinding(Queue queue, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(paymentWebhookExchange())
                .with(routingKey);
    }

    private Queue retryQueue(String name, int ttl) {
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", ttl)
                .withArgument("x-dead-letter-exchange", PAYMENT_WEBHOOK_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PAYMENT_WEBHOOK_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange paymentWebhookExchange() {
        return new DirectExchange(PAYMENT_WEBHOOK_EXCHANGE);
    }

    @Bean
    public Queue paymentWebhookQueue() {
        return QueueBuilder.durable(PAYMENT_WEBHOOK_QUEUE).build();
    }

    @Bean
    public Queue parkingLotQueue() {
        return QueueBuilder.durable(PAYMENT_WEBHOOK_PARKING_LOT_QUEUE).build();
    }

    @Bean public Queue retry1a() { return retryQueue(RETRY_1A, 5_000); }
    @Bean public Queue retry1b() { return retryQueue(RETRY_1B, 8_000); }

    @Bean public Queue retry2a() { return retryQueue(RETRY_2A, 20_000); }
    @Bean public Queue retry2b() { return retryQueue(RETRY_2B, 30_000); }

    @Bean public Queue retry3a() { return retryQueue(RETRY_3A, 60_000); }
    @Bean public Queue retry3b() { return retryQueue(RETRY_3B, 90_000); }

    @Bean public Queue retry4a() { return retryQueue(RETRY_4A, 120_000); }
    @Bean public Queue retry4b() { return retryQueue(RETRY_4B, 150_000); }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(paymentWebhookQueue())
                .to(paymentWebhookExchange())
                .with(PAYMENT_WEBHOOK_QUEUE);
    }

    @Bean
    public Binding parkingLotBinding() {
        return BindingBuilder.bind(parkingLotQueue())
                .to(paymentWebhookExchange())
                .with(PAYMENT_WEBHOOK_PARKING_LOT_QUEUE);
    }

    @Bean public Binding b1a() { return retryBinding(retry1a(), RETRY_1A); }
    @Bean public Binding b1b() { return retryBinding(retry1b(), RETRY_1B); }
    @Bean public Binding b2a() { return retryBinding(retry2a(), RETRY_2A); }
    @Bean public Binding b2b() { return retryBinding(retry2b(), RETRY_2B); }
    @Bean public Binding b3a() { return retryBinding(retry3a(), RETRY_3A); }
    @Bean public Binding b3b() { return retryBinding(retry3b(), RETRY_3B); }
    @Bean public Binding b4a() { return retryBinding(retry4a(), RETRY_4A); }
    @Bean public Binding b4b() { return retryBinding(retry4b(), RETRY_4B); }
}


