package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.services.WebhookService;
import com.example.cinema.api.infrastructure.mercadopago.dtos.MercadoPagoWebhookNotificationDTO;
import com.example.cinema.api.infrastructure.repositories.PaymentRepository;
import com.example.cinema.api.shared.exceptions.WebhookException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebhookMercadoPagoService implements WebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;

    public WebhookMercadoPagoService(PaymentRepository paymentRepository, PaymentClient paymentClient, ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void processWebhook(String payload) {
        MercadoPagoWebhookNotificationDTO webhookPayload;
        try {
            webhookPayload = objectMapper.readValue(payload, MercadoPagoWebhookNotificationDTO.class);
        } catch (Exception e) {
            log.error("Erro ao desserializar payload do webhook do Mercado Pago: {}", e.getMessage());
            throw new WebhookException("Erro ao processar webhook do Mercado Pago", e);
        }

        try {
            Payment payment = paymentClient.get(webhookPayload.getResourceId());

            Long externalReference = Long.parseLong(payment.getExternalReference());
            String status = payment.getStatus();

            com.example.cinema.api.domain.entities.Payment paymentEntity = paymentRepository.findById(externalReference)
                    .orElseThrow(() -> new RuntimeException(
                            "Pagamento não encontrado para o ID externo: " + externalReference
                    ));

            PaymentStatus paymentStatus = PaymentStatus.fromValue(status);
            paymentEntity.setPaymentStatus(paymentStatus);

            paymentRepository.save(paymentEntity);

        } catch (Exception e) {
            log.error("Erro ao processar pagamento do webhook do Mercado Pago: {}", e.getMessage(), e);
            throw new WebhookException("Erro ao processar pagamento", e);
        }

        // TODO: enviar email de atualização de status de compra para o usuário SE status mudou para aprovado

    }



}
