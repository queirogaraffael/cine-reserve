package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.payment.PaymentGatewayInterface;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.BoletoPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.BoletoPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.CardPaymentResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PixPaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ApiPagamentoException;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerAddressRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MercadoPagoGateway implements PaymentGatewayInterface {

    private final PaymentClient paymentClient;
    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    public MercadoPagoGateway(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public PixPaymentResponseDTO createPixPayment(Purchase purchase, User user, PixPaymentRequestDTO request, String idempotencyKey) {

        try{
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

            String pixCopiaECola = payment.getPointOfInteraction().getTransactionData().getQrCode();
            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
            String instrucoesUrl = payment.getPointOfInteraction().getTransactionData().getTicketUrl();

            Long paymentId = Long.parseLong(payment.getId().toString());
            String paymentStatus = payment.getStatus();

            return new PixPaymentResponseDTO(
                    paymentId,
                    paymentStatus,
                    pixCopiaECola,
                    qrCodeBase64,
                    instrucoesUrl
            );

        }catch (MPException | MPApiException e){
            throw new ApiPagamentoException("Erro ao processar pagamento PIX: " + e.getMessage(), e);
        }

    }

    @Override
    public CardPaymentResponseDTO createCardPayment(Purchase purchase, User user, CardPaymentRequestDTO request, String idempotencyKey) {

        try{
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

            Long paymentMercadoPagoId = Long.parseLong(payment.getId().toString());
            String paymentStatus = payment.getStatus();

            String lastFourDigits =  payment.getCard().getLastFourDigits();

            Integer installments = payment.getInstallments();
            String paymentMethodId = payment.getPaymentMethodId();

            return new CardPaymentResponseDTO(
                    paymentMercadoPagoId,
                    paymentStatus,
                    lastFourDigits,
                    installments,
                    paymentMethodId
            );
        }catch (MPException | MPApiException e){
            throw new ApiPagamentoException("Erro ao processar pagamento de CARTÃO: " + e.getMessage(), e);
        }

    }

    @Override
    public BoletoPaymentResponseDTO createBoletoPayment(Purchase purchase, User user, BoletoPaymentRequestDTO paymentBoletoRequestDTO, String idempotencyKey) {

        try {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(java.util.Collections.singletonMap(IDEMPOTENCY_KEY_HEADER, idempotencyKey))
                    .build();

            IdentificationRequest identificationRequest = IdentificationRequest.builder()
                    .type("CPF")
                    .number(user.getCpf())
                    .build();

            PaymentPayerAddressRequest addressRequest = PaymentPayerAddressRequest.builder()
                    .zipCode(paymentBoletoRequestDTO.getZipCode())
                    .streetName(paymentBoletoRequestDTO.getStreetName())
                    .streetNumber(paymentBoletoRequestDTO.getStreetNumber())
                    .neighborhood(paymentBoletoRequestDTO.getNeighborhood())
                    .city(paymentBoletoRequestDTO.getCityName())
                    .federalUnit(paymentBoletoRequestDTO.getFederalUnit())
                    .build();

            PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                    .email(user.getEmail())
                    .identification(identificationRequest)
                    .address(addressRequest)
                    .build();

            ZonedDateTime expirationDate = ZonedDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS);

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .transactionAmount(purchase.getTotalPrice())
                    .description("Pedido do Cliente " + user.getName())
                    .paymentMethodId("bolbradesco")
                    .payer(payerRequest)
                    .externalReference(purchase.getId().toString())
                    .dateOfExpiration(expirationDate.toOffsetDateTime())
                    .build();

            Payment payment = paymentClient.create(paymentCreateRequest, requestOptions);

            Long paymentId = Long.parseLong(payment.getId().toString());
            String paymentStatus = payment.getStatus();
            OffsetDateTime expirationDateBoleto = payment.getDateOfExpiration();

            String boletoUrl = payment.getTransactionDetails().getExternalResourceUrl();
            String linhaDigitavel = payment.getTransactionDetails().getBarcode().getContent();

            return new BoletoPaymentResponseDTO(
                    paymentId,
                    paymentStatus,
                    boletoUrl,
                    linhaDigitavel,
                    expirationDateBoleto
            );
        } catch (MPException | MPApiException e) {
            throw new ApiPagamentoException("Erro ao processar pagamento BOLETO: " + e.getMessage(), e);
        }
    }}