package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.application.service.gateway.CardPaymentGatewayPort;
import com.example.cinema.api.application.service.gateway.PixPaymentGatewayPort;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;
import com.example.cinema.api.domain.payment.exception.GatewayUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.example.cinema.api.domain.order.exception.InvalidPaymentAmountException;
import com.example.cinema.api.infrastructure.exception.ApiPagamentoException;
import com.example.cinema.api.application.dto.payment.PaymentAddressDTO;
import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerAddressRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Component
@Slf4j
public class MercadoPagoGatewayService implements PixPaymentGatewayPort, CardPaymentGatewayPort {

    private final PaymentClient paymentClientMercadoPago;

    @Value("${cinereserve.payment.pix.expiration-minutes}")
    private long pixExpirationMinutes;

    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    public MercadoPagoGatewayService(PaymentClient paymentClientMercadoPago) {
        this.paymentClientMercadoPago = paymentClientMercadoPago;
    }

    @Override
    @CircuitBreaker(name = "mercadopago-gateway", fallbackMethod = "pixPaymentFallback")
    public PixGatewayResult createPixPayment(OrderPaymentContext purchase, PaymentUserContext user, PixPaymentRequestDTO request) {
        validateTotalPrice(purchase);

        try {
            MPRequestOptions requestOptions = buildRequestOptions(purchase.idempotencyKey());
            PaymentPayerRequest payerRequest = buildPayerRequest(user);

            ZonedDateTime expirationDate = ZonedDateTime.now(ZoneOffset.UTC)
                    .plusMinutes(pixExpirationMinutes)
                    .truncatedTo(ChronoUnit.SECONDS);

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(purchase.totalPrice())
                    .description("CineReserve - Compra #" + purchase.id())
                    .paymentMethodId("pix")
                    .payer(payerRequest)
                    .externalReference(purchase.id().toString())
                    .dateOfExpiration(expirationDate.toOffsetDateTime())
                    .build();

            log.info("Iniciando criação de pagamento PIX. purchaseId={}", purchase.id());

            Payment mpPayment = paymentClientMercadoPago.create(paymentCreateRequest, requestOptions);

            log.info("Pagamento PIX criado. purchaseId={} transactionId={} status={}",
                    purchase.id(), mpPayment.getId(), mpPayment.getStatus());
            log.info("AUDIT: Dados pessoais transmitidos ao Mercado Pago. purchaseId={} dataTypes=[cpf,email]",
                    purchase.id());

            if (mpPayment.getPointOfInteraction() == null || mpPayment.getPointOfInteraction().getTransactionData() == null) {
                throw new ApiPagamentoException("Resposta do PIX sem dados de transação. transactionId=" + mpPayment.getId(), null);
            }

            var data = mpPayment.getPointOfInteraction().getTransactionData();

            ZonedDateTime confirmedExpiration = (mpPayment.getDateOfExpiration() != null)
                    ? mpPayment.getDateOfExpiration().toZonedDateTime()
                    : expirationDate;

            return new PixGatewayResult(
                    String.valueOf(mpPayment.getId()),
                    mpPayment.getStatus(),
                    mpPayment.getStatusDetail(),
                    data.getQrCode(),
                    data.getQrCodeBase64(),
                    data.getTicketUrl(),
                    confirmedExpiration
            );

        } catch (ApiPagamentoException e) {
            throw e;
        } catch (MPException | MPApiException e) {
            log.error("Falha na API do Mercado Pago ao processar PIX. purchaseId={}", purchase.id(), e);
            throw new ApiPagamentoException("Falha na API do provedor de pagamento ao processar PIX", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar PIX. purchaseId={}", purchase.id(), e);
            throw new ApiPagamentoException("Erro inesperado ao processar resposta do PIX", e);
        }
    }

    public PixGatewayResult pixPaymentFallback(OrderPaymentContext purchase, PaymentUserContext user, PixPaymentRequestDTO request, Throwable ex) {
        if (ex instanceof InvalidPaymentAmountException) throw (InvalidPaymentAmountException) ex;
        log.error("Circuit Breaker aberto/falha para PIX. purchaseId={}", purchase.id(), ex);
        throw new GatewayUnavailableException("Serviço de pagamento PIX temporariamente indisponível. Tente novamente em alguns instantes.");
    }

    @Override
    @CircuitBreaker(name = "mercadopago-gateway", fallbackMethod = "cardPaymentFallback")
    public CardGatewayResult createCardPayment(OrderPaymentContext purchase, PaymentUserContext user, CardPaymentRequestDTO request) {
        validateTotalPrice(purchase);

        try {
            MPRequestOptions requestOptions = buildRequestOptions(purchase.idempotencyKey());
            PaymentPayerRequest payerRequest = buildPayerRequest(user, request.getPaymentAddressDTO());

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(purchase.totalPrice())
                    .description("CineReserve - Compra #" + purchase.id())
                    .paymentMethodId(request.getPaymentMethodId())
                    .token(request.getCardToken())
                    .installments(request.getInstallments())
                    .payer(payerRequest)
                    .externalReference(purchase.id().toString())
                    .build();

            log.info("Iniciando criação de pagamento com cartão. purchaseId={}", purchase.id());

            Payment mpPayment = paymentClientMercadoPago.create(paymentCreateRequest, requestOptions);

            log.info("Pagamento com cartão criado. purchaseId={} transactionId={} status={}",
                    purchase.id(), mpPayment.getId(), mpPayment.getStatus());
            log.info("AUDIT: Dados pessoais transmitidos ao Mercado Pago. purchaseId={} dataTypes=[cpf,email,address]",
                    purchase.id());

            String lastFourDigits = (mpPayment.getCard() != null)
                    ? mpPayment.getCard().getLastFourDigits()
                    : null;

            return new CardGatewayResult(
                    String.valueOf(mpPayment.getId()),
                    mpPayment.getStatus(),
                    mpPayment.getStatusDetail(),
                    lastFourDigits,
                    mpPayment.getInstallments(),
                    mpPayment.getPaymentMethodId()
            );

        } catch (MPException | MPApiException e) {
            log.error("Falha na API do Mercado Pago ao processar cartão. purchaseId={}", purchase.id(), e);
            throw new ApiPagamentoException("Falha na API do provedor de pagamento ao processar pagamento com cartão", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar pagamento com cartão. purchaseId={}", purchase.id(), e);
            throw new ApiPagamentoException("Erro inesperado ao processar resposta do pagamento com cartão", e);
        }
    }

    public CardGatewayResult cardPaymentFallback(OrderPaymentContext purchase, PaymentUserContext user, CardPaymentRequestDTO request, Throwable ex) {
        if (ex instanceof InvalidPaymentAmountException) throw (InvalidPaymentAmountException) ex;
        log.error("Circuit Breaker aberto/falha para cartão. purchaseId={}", purchase.id(), ex);
        throw new GatewayUnavailableException("Serviço de pagamento com cartão temporariamente indisponível. Tente novamente em alguns instantes.");
    }

    private void validateTotalPrice(OrderPaymentContext purchase) {
        if (purchase.totalPrice() == null || purchase.totalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException("Valor total da compra deve ser maior que zero. purchaseId=" + purchase.id());
        }
    }

    private MPRequestOptions buildRequestOptions(String idempotencyKey) {
        return MPRequestOptions.builder()
                .customHeaders(Collections.singletonMap(IDEMPOTENCY_KEY_HEADER, idempotencyKey))
                .build();
    }

    private PaymentPayerRequest buildPayerRequest(PaymentUserContext user) {
        IdentificationRequest identification = IdentificationRequest.builder()
                .type("CPF")
                .number(user.cpf())
                .build();

        return PaymentPayerRequest.builder()
                .email(user.email())
                .identification(identification)
                .build();
    }

    private PaymentPayerRequest buildPayerRequest(PaymentUserContext user, PaymentAddressDTO address) {
        IdentificationRequest identification = IdentificationRequest.builder()
                .type("CPF")
                .number(user.cpf())
                .build();

        PaymentPayerAddressRequest addressRequest = PaymentPayerAddressRequest.builder()
                .zipCode(address.zipCode())
                .streetName(address.streetName())
                .streetNumber(address.streetNumber())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.federalUnit())
                .build();

        return PaymentPayerRequest.builder()
                .email(user.email())
                .identification(identification)
                .address(addressRequest)
                .build();
    }
}
