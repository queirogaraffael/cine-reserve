package com.example.cinema.api.infrastructure.mercadopago;

import com.example.cinema.api.application.dto.payment.PaymentOrderContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.fixtures.PaymentFixture;
import com.example.cinema.api.application.dto.payment.response.gateway.card.CardGatewayResult;
import com.example.cinema.api.application.dto.payment.response.gateway.pix.PixGatewayResult;
import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.infrastructure.exception.ApiPagamentoException;
import com.example.cinema.api.infrastructure.mercadopago.services.MercadoPagoGatewayService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentCard;
import com.mercadopago.resources.payment.PaymentPointOfInteraction;
import com.mercadopago.resources.payment.PaymentTransactionData;
import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MercadoPagoGatewayServiceTest {

    @Mock
    private PaymentClient paymentClientMercadoPago;

    @InjectMocks
    private MercadoPagoGatewayService service;

    private PaymentOrderContext purchase;
    private PaymentUserContext user;
    private PixPaymentRequestDTO pixRequest;
    private CardPaymentRequestDTO cardRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "pixExpirationMinutes", 30L);

        purchase = PaymentFixture.validPurchaseContext();
        user = PaymentFixture.validUserContext();
        pixRequest = PaymentFixture.validPixRequest();
        cardRequest = PaymentFixture.validCardRequest();
    }

    @Test
    void createPixPayment_deveRetornarPixGatewayResult_quandoPagamentoCriadoComSucesso() throws MPException, MPApiException {
        PaymentTransactionData transactionData = mock(PaymentTransactionData.class);
        when(transactionData.getQrCode()).thenReturn("qr-code-string");
        when(transactionData.getQrCodeBase64()).thenReturn("qr-code-base64");
        when(transactionData.getTicketUrl()).thenReturn("https://mercadopago.com/ticket");

        PaymentPointOfInteraction pointOfInteraction = mock(PaymentPointOfInteraction.class);
        when(pointOfInteraction.getTransactionData()).thenReturn(transactionData);

        Payment payment = mock(Payment.class);
        when(payment.getId()).thenReturn(123L);
        when(payment.getStatus()).thenReturn("pending");
        when(payment.getStatusDetail()).thenReturn("pending_waiting_transfer");
        when(payment.getPointOfInteraction()).thenReturn(pointOfInteraction);
        when(payment.getDateOfExpiration()).thenReturn(null);

        when(paymentClientMercadoPago.create(any(), any())).thenReturn(payment);

        PixGatewayResult result = service.createPixPayment(purchase, user, pixRequest);

        assertNotNull(result);
        assertEquals("123", result.providerPaymentId());
        assertEquals("pending", result.status());
        assertEquals("qr-code-string", result.pixCopiaECola());
        assertEquals("qr-code-base64", result.qrCodeBase64());
        assertEquals("https://mercadopago.com/ticket", result.instrucoesUrl());
        assertNotNull(result.expirationDate());
    }

    @Test
    void createPixPayment_deveLancarApiPagamentoException_quandoTransactionDataForNula() throws MPException, MPApiException {
        Payment payment = mock(Payment.class);
        when(payment.getPointOfInteraction()).thenReturn(null);

        when(paymentClientMercadoPago.create(any(), any())).thenReturn(payment);

        assertThrows(ApiPagamentoException.class,
                () -> service.createPixPayment(purchase, user, pixRequest));
    }

    @Test
    void createPixPayment_deveLancarApiPagamentoException_quandoMercadoPagoRetornarErro() throws MPException, MPApiException {
        when(paymentClientMercadoPago.create(any(), any())).thenThrow(mock(MPApiException.class));

        assertThrows(ApiPagamentoException.class,
                () -> service.createPixPayment(purchase, user, pixRequest));
    }

    @Test
    void createPixPayment_deveLancarInvalidPaymentAmountException_quandoValorForZero() {
        assertThrows(PaymentValidationException.class,
                () -> service.createPixPayment(PaymentFixture.zeroPurchaseContext(), user, pixRequest));
    }

    @Test
    void createCardPayment_deveRetornarCardGatewayResult_quandoPagamentoCriadoComSucesso() throws MPException, MPApiException {
        Payment payment = mock(Payment.class);
        when(payment.getId()).thenReturn(456L);
        when(payment.getStatus()).thenReturn("approved");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getInstallments()).thenReturn(1);
        when(payment.getPaymentMethodId()).thenReturn("visa");
        when(payment.getCard()).thenReturn(null);

        when(paymentClientMercadoPago.create(any(), any())).thenReturn(payment);

        CardGatewayResult result = service.createCardPayment(purchase, user, cardRequest);

        assertNotNull(result);
        assertEquals("456", result.providerPaymentId());
        assertEquals("approved", result.status());
        assertEquals("accredited", result.statusDetail());
        assertEquals(1, result.installments());
        assertEquals("visa", result.paymentMethodId());
        assertNull(result.lastFourDigits());
    }

    @Test
    void createCardPayment_deveRetornarLastFourDigits_quandoCardNaoForNulo() throws MPException, MPApiException {

        PaymentCard card = mock(PaymentCard.class);
        when(card.getLastFourDigits()).thenReturn("1234");

        Payment payment = mock(Payment.class);
        when(payment.getId()).thenReturn(456L);
        when(payment.getStatus()).thenReturn("approved");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getInstallments()).thenReturn(1);
        when(payment.getPaymentMethodId()).thenReturn("visa");
        when(payment.getCard()).thenReturn(card);

        when(paymentClientMercadoPago.create(any(), any())).thenReturn(payment);

        CardGatewayResult result = service.createCardPayment(purchase, user, cardRequest);

        assertEquals("1234", result.lastFourDigits());
    }

    @Test
    void createCardPayment_deveLancarApiPagamentoException_quandoMercadoPagoRetornarErro() throws MPException, MPApiException {
        when(paymentClientMercadoPago.create(any(), any())).thenThrow(mock(MPApiException.class));

        assertThrows(ApiPagamentoException.class,
                () -> service.createCardPayment(purchase, user, cardRequest));
    }

    @Test
    void createCardPayment_deveLancarInvalidPaymentAmountException_quandoValorForZero() {
        assertThrows(PaymentValidationException.class,
                () -> service.createCardPayment(PaymentFixture.zeroPurchaseContext(), user, cardRequest));
    }
}