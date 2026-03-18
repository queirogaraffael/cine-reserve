package com.example.cinema.api.infrastructure.messaging.rabbitmq;

import com.example.cinema.api.infrastructure.messaging.rabbitmq.config.RabbitMQPaymentWebhookConfig;
import com.example.cinema.api.application.service.ExternalPaymentProvider;
import com.example.cinema.api.application.service.PaymentUpdateService;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.application.dto.webhook.PaymentWebhookEvent;
import com.example.cinema.api.application.exception.ExternalServiceTemporaryException;
import com.example.cinema.api.infrastructure.messaging.rabbitmq.exception.MaxRetriesExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PaymentWebhookConsumer {

    private final ExternalPaymentProvider paymentQueryService;
    private final PaymentWebhookRetryPolicy retryPolicy;
    private final PaymentUpdateService paymentUpdateService;
    private final PaymentFailureHandler failureHandler;

    public PaymentWebhookConsumer(
            ExternalPaymentProvider paymentQueryService,
            PaymentWebhookRetryPolicy retryPolicy,
            PaymentUpdateService paymentUpdateService,
            PaymentFailureHandler failureHandler
    ) {
        this.paymentQueryService = paymentQueryService;
        this.retryPolicy = retryPolicy;
        this.paymentUpdateService = paymentUpdateService;
        this.failureHandler = failureHandler;
    }

    @RabbitListener(queues = RabbitMQPaymentWebhookConfig.PAYMENT_WEBHOOK_QUEUE)
    public void processPayment(PaymentWebhookEvent event, Message message) {

        int retries = retryPolicy.getRetryCount(message);

        try {
            ExternalPaymentSnapshot externalPaymentSnapshot = paymentQueryService.getPayment(event.getPaymentId());

            paymentUpdateService.processPaymentUpdate(externalPaymentSnapshot, event);

            log.info("Pagamento {} processado com sucesso.", event.getPaymentId());

        } catch (ExternalServiceTemporaryException e) {

            try {
                retryPolicy.retry(event, message, retries, e.getMessage());
                return;
            } catch (MaxRetriesExceededException ex) {
                log.error("Número máximo de tentativas excedido para o pagamento {}. Enviando para parking lot.", event.getPaymentId(), ex);

                failureHandler.sendToParkingLot(event);
                return;
            }

        } catch (Exception e) {
            log.error("Erro não recuperável ao processar o pagamento {}. Enviando para parking lot.", event.getPaymentId(), e);

            failureHandler.sendToParkingLot(event);
        }
    }

}



