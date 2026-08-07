package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.application.service.ExternalPaymentProvider;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.application.exception.ExternalServicePermanentException;
import com.example.cinema.api.application.exception.ExternalServiceTemporaryException;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoPaymentService implements ExternalPaymentProvider {

    private final PaymentClient paymentClient;

    public MercadoPagoPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    // Separei em erros transitorios e erros definitivos para que o consumer saiba para qual fila enviar
    @Override
    public ExternalPaymentSnapshot getPayment(String providerPaymentId) {

        try {
            Payment mpPayment = paymentClient.get(Long.valueOf(providerPaymentId));

            return new ExternalPaymentSnapshot(mpPayment.getExternalReference() != null ? Long.valueOf(mpPayment.getExternalReference()) : null,
                    mpPayment.getStatus(),
                    mpPayment.getStatusDetail(),
                    "MERCADO_PAGO_WEBHOOK"
            );

        } catch (MPApiException e) {

            int status = e.getStatusCode();

            if (status == 404 || status == 408 || status == 429 || (status >= 500 && status <= 599)) {
                throw new ExternalServiceTemporaryException(
                        "Erro temporário ao consultar o pagamento no Mercado Pago. paymentId=" + providerPaymentId + ", statusHTTP=" + status +
                                ", mensagem=" + e.getMessage());
            }

            throw new ExternalServicePermanentException(
                    "Erro permanente ao consultar o pagamento no Mercado Pago. paymentId=" + providerPaymentId + ", statusHTTP=" + status +
                            ", mensagem=" + e.getMessage());

        } catch (MPException e) {
            throw new ExternalServiceTemporaryException(
                    "Erro interno do SDK do Mercado Pago ao consultar o pagamento. paymentId=" + providerPaymentId + ", mensagem=" + e.getMessage());

        } catch (Exception e) {
            throw new ExternalServicePermanentException("Erro inesperado ao consultar pagamento externo. paymentId=" + providerPaymentId +
                    ", mensagem=" + e.getMessage());
        }
    }


}
