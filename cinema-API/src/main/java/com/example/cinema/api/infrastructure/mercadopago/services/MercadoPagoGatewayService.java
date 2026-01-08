package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.services.PaymentGatewayService;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.CardPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PixPaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ApiPagamentoException;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MercadoPagoGatewayService implements PaymentGatewayService {

    private final PaymentClient paymentClient;
    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    public MercadoPagoGatewayService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public PixPaymentResponseDTO createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request, String idempotencyKey) {

        try {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(java.util.Collections.singletonMap(IDEMPOTENCY_KEY_HEADER, idempotencyKey))
                    .build();

            IdentificationRequest identificationRequest = IdentificationRequest.builder()
                    .type("CPF")
                    .number(user.getCpf())
                    .build();

            PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                    .email(user.getEmail())
                    .identification(identificationRequest)
                    .build();

            ZonedDateTime expirationDate = ZonedDateTime.now().plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(purchase.getTotalPrice())
                    .description("Pedido do Cliente " + user.getName())
                    .paymentMethodId("pix")
                    .payer(payerRequest)
                    .externalReference(purchase.getId().toString())
                    .dateOfExpiration(expirationDate.toOffsetDateTime())
                    .build();

            Payment payment = paymentClient.create(paymentCreateRequest, requestOptions);

            ZonedDateTime expirationConfirmada = (payment.getDateOfExpiration() != null)
                    ? payment.getDateOfExpiration().toZonedDateTime()
                    : expirationDate;

            String pixCopiaECola = "";
            String qrCodeBase64 = "";
            String instrucoesUrl = "";

            if (payment.getPointOfInteraction() != null && payment.getPointOfInteraction().getTransactionData() != null) {
                var data = payment.getPointOfInteraction().getTransactionData();
                pixCopiaECola = data.getQrCode();
                qrCodeBase64 = data.getQrCodeBase64();
                instrucoesUrl = data.getTicketUrl();
            }

            Long paymentId = (payment.getId() != null) ? Long.parseLong(payment.getId().toString()) : null;
            String paymentStatus = payment.getStatus();

            return new PixPaymentResponseDTO(
                    paymentId,
                    paymentStatus,
                    pixCopiaECola,
                    qrCodeBase64,
                    instrucoesUrl,
                    expirationConfirmada
            );

        } catch (MPException | MPApiException e) {
            throw new ApiPagamentoException("Erro na API do Mercado Pago ao processar PIX: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ApiPagamentoException("Erro interno inesperado ao processar resposta do PIX: " + e.getMessage(), e);
        }
    }

    @Override
    public CardPaymentResponseDTO createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request, String idempotencyKey) {

        try {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(java.util.Collections.singletonMap(IDEMPOTENCY_KEY_HEADER, idempotencyKey))
                    .build();

            IdentificationRequest identificationRequest = IdentificationRequest.builder()
                    .type("CPF")
                    .number(user.getCpf())
                    .build();

            PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                    .email(user.getEmail())
                    .identification(identificationRequest)
                    .build();

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(purchase.getTotalPrice())
                    .description("Pedido do Cliente " + user.getName())
                    .paymentMethodId(request.getPaymentMethodId())
                    .token(request.getCardToken())
                    .installments(request.getInstallments())
                    .payer(payerRequest)
                    .externalReference(purchase.getId().toString())
                    .build();

            Payment payment = paymentClient.create(paymentCreateRequest, requestOptions);

            Long paymentMercadoPagoId = (payment.getId() != null) ? Long.parseLong(payment.getId().toString()) : null;
            String paymentStatus = payment.getStatus();
            String statusDetail = payment.getStatusDetail();
            Integer installments = payment.getInstallments();
            String paymentMethodId = payment.getPaymentMethodId();

            String lastFourDigits = (payment.getCard() != null) ? payment.getCard().getLastFourDigits() : "N/A";

            return new CardPaymentResponseDTO(
                    paymentMercadoPagoId,
                    paymentStatus,
                    statusDetail,
                    lastFourDigits,
                    installments,
                    paymentMethodId
            );

        } catch (MPException | MPApiException e) {
            throw new ApiPagamentoException("Erro na API do Mercado Pago ao processar CARTÃO: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ApiPagamentoException("Erro interno inesperado ao processar resposta de pagamento: " + e.getMessage(), e);
        }
    }
}