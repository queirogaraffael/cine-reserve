package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.services.WebhookService;
import com.example.cinema.api.infrastructure.mercadopago.dtos.MercadoPagoWebhookDTO;
import com.example.cinema.api.infrastructure.repositories.PaymentRepository;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.exceptions.WebhookException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
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
        MercadoPagoWebhookDTO webhook;

        try {
            webhook = objectMapper.readValue(payload, MercadoPagoWebhookDTO.class);
        } catch (Exception e) {
            log.error("Erro crítico de desserialização: {}", e.getMessage());
            return;
        }

        if (!"payment".equals(webhook.getType())) {
            log.info("Webhook recebido de tipo ignorado: {}", webhook.getType());
            return;
        }

        try {
            Payment paymentMercadoPago;
            int tentativas = 0;

            while (true) {
                try {
                    paymentMercadoPago = paymentClient.get(webhook.getData().getId());
                    break;
                } catch (MPApiException e) {
                    tentativas++;
                    if (tentativas >= 3 || e.getStatusCode() != 404) throw e;

                    log.warn("Pagamento não encontrado (tentativa {}/3). Aguardando para reprocessar...", tentativas);
                    Thread.sleep(2000);
                }
            }

            if (paymentMercadoPago.getExternalReference() == null) {
                log.warn("Pagamento {} do MP sem external_reference. Ignorando.", paymentMercadoPago.getId());
                return;
            }

            Long purchaseId = Long.parseLong(paymentMercadoPago.getExternalReference());

            com.example.cinema.api.domain.entities.Payment paymentLocal = paymentRepository.findByPurchaseId(purchaseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para PurchaseId: " + purchaseId));

            PaymentStatus novoStatus = PaymentStatus.fromValue(paymentMercadoPago.getStatus());
            String statusDetail = paymentMercadoPago.getStatusDetail();

            log.info("Processando Webhook - PurchaseId: {} | Status: {} | Detalhe: {}", purchaseId, novoStatus, statusDetail);

            paymentLocal.setPaymentStatus(novoStatus);
            paymentLocal.setStatusDetail(statusDetail);

            paymentRepository.save(paymentLocal);

        } catch (NumberFormatException e) {
            log.error("ID externo (PurchaseId) inválido vindo do Mercado Pago: {}", e.getMessage());
        } catch (MPApiException e) {
            log.error("Erro na API do Mercado Pago ao consultar ID {}: Status {}", webhook.getData().getId(), e.getStatusCode());
            throw new WebhookException("Erro ao consultar Mercado Pago", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar webhook: {}", e.getMessage(), e);
            throw new WebhookException("Erro processamento", e);
        }
    }
}
