package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.configs.RabbitMQPaymentWebhookConfig;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.shared.dtos.webhook.PaymentWebhookEvent;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class PaymentWebhookConsumer {

    private static final int MAX_RETRIES = 4;
    private static final String RETRY_HEADER = "x-retry-count";

    private final RabbitTemplate rabbitTemplate;
    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final PaymentClient paymentClient;
    private final Random random = new Random();

    public PaymentWebhookConsumer(RabbitTemplate rabbitTemplate, PaymentRepositoryJpa paymentRepositoryJpa, PaymentClient paymentClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.paymentClient = paymentClient;
    }


    @RabbitListener(queues = RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_QUEUE)
    public void processPayment(PaymentWebhookEvent event, Message message) {

        int retries = getRetryCount(message);

        try {
            Payment paymentMP = paymentClient.get(event.getPaymentId());
            processPaymentUpdate(paymentMP, event);

            log.info("Pagamento {} processado com sucesso", event.getPaymentId());

        } catch (MPApiException e) {

            if (isRetryableMpError(e)) {
                retry(event, message, retries,
                        "Erro transitório da API Mercado Pago: " + e.getStatusCode());
                return;
            }

            log.error("Erro definitivo da API MP para pagamento {}", event.getPaymentId(), e);
            sendToParkingLot(event);

        } catch (Exception e) {
            log.error("Erro inesperado processando pagamento {}", event.getPaymentId(), e);
            sendToParkingLot(event);
        }
    }

    private void retry(PaymentWebhookEvent event, Message message, int retries, String reason) {

        if (retries >= MAX_RETRIES) {
            log.error("Pagamento {} excedeu retries. Indo para parking lot", event.getPaymentId());
            sendToParkingLot(event);
            return;
        }

        String routingKey = chooseRetryRoutingKey(retries + 1);

        MessageProperties props = message.getMessageProperties();
        props.getHeaders().put(RETRY_HEADER, retries + 1);

        Message retryMessage = new Message(message.getBody(), props);

        rabbitTemplate.send(RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_EXCHANGE, routingKey, retryMessage);

        log.warn("Retry {} agendado para pagamento {} ({}) via {}",
                retries + 1, event.getPaymentId(), reason, routingKey);
    }

    private int getRetryCount(Message message) {
        Object value = message.getMessageProperties().getHeaders().get(RETRY_HEADER);

        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return 0;
        }
    }

    private String chooseRetryRoutingKey(int tentativa) {
        boolean jitter = random.nextBoolean();

        return switch (tentativa) {
            case 1 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_1A : RabbitMQPaymentWebhookConfig.RETRY_1B;
            case 2 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_2A : RabbitMQPaymentWebhookConfig.RETRY_2B;
            case 3 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_3A : RabbitMQPaymentWebhookConfig.RETRY_3B;
            case 4 -> jitter ? RabbitMQPaymentWebhookConfig.RETRY_4A : RabbitMQPaymentWebhookConfig.RETRY_4B;
            default -> RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_PARKING_LOT_QUEUE;
        };
    }

    private boolean isRetryableMpError(MPApiException e) {
        int status = e.getStatusCode();

        if (status == 404) return true;
        if (status == 408) return true;
        if (status == 429) return true;

        return status >= 500 && status <= 599;
    }

    private void sendToParkingLot(PaymentWebhookEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_EXCHANGE, RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_PARKING_LOT_QUEUE, event);
    }

    private void processPaymentUpdate(Payment paymentMercadoPago, PaymentWebhookEvent event) {

        if (paymentMercadoPago.getExternalReference() == null) {
            throw new IllegalStateException("Pagamento sem external_reference");
        }

        Long purchaseId = Long.parseLong(paymentMercadoPago.getExternalReference());

        var paymentLocal = paymentRepositoryJpa.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pagamento não encontrado para PurchaseId: " + purchaseId));

        if (paymentLocal.getVersion() > event.getVersion()) {
            log.info("Evento desatualizado ignorado para pagamento {}", purchaseId);
            return;
        }

        PaymentStatus novoStatus = PaymentStatus.fromValue(paymentMercadoPago.getStatus());

        if (paymentLocal.getPaymentStatus() == novoStatus) {
            log.info("Pagamento {} já está no status {}", purchaseId, novoStatus);
            return;
        }

        paymentLocal.setPaymentStatus(
                paymentLocal.getPaymentStatus().transitionTo(novoStatus)
        );

        paymentLocal.setStatusDetail(paymentMercadoPago.getStatusDetail());

        paymentRepositoryJpa.save(paymentLocal);
    }
}


