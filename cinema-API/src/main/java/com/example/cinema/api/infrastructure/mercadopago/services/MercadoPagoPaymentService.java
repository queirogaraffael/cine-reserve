package com.example.cinema.api.infrastructure.mercadopago.services;

import com.example.cinema.api.domain.service.ExternalPaymentProvider;
import com.example.cinema.api.shared.dtos.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.shared.exceptions.ExternalServicePermanentException;
import com.example.cinema.api.shared.exceptions.ExternalServiceTemporaryException;
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
    public ExternalPaymentSnapshot getPayment(Long paymentId) {

        try {
            Payment payment = paymentClient.get(paymentId);

            return new ExternalPaymentSnapshot(payment.getExternalReference() != null ? Long.valueOf(payment.getExternalReference()) : null,
                    payment.getStatus(),
                    payment.getStatusDetail()
            );

        } catch (MPApiException e) {

            int status = e.getStatusCode();

            if (status == 404 || status == 408 || status == 429 || (status >= 500 && status <= 599)) {
                throw new ExternalServiceTemporaryException(
                        "Erro temporário ao consultar o pagamento no Mercado Pago. paymentId=" + paymentId + ", statusHTTP=" + status +
                                ", mensagem=" + e.getMessage());
            }

            throw new ExternalServicePermanentException(
                    "Erro permanente ao consultar o pagamento no Mercado Pago. paymentId=" + paymentId + ", statusHTTP=" + status +
                            ", mensagem=" + e.getMessage());

        } catch (MPException e) {
            throw new ExternalServiceTemporaryException(
                    "Erro interno do SDK do Mercado Pago ao consultar o pagamento. paymentId=" + paymentId + ", mensagem=" + e.getMessage());

        } catch (Exception e) {
            throw new ExternalServicePermanentException("Erro inesperado ao consultar pagamento externo. paymentId=" + paymentId +
                    ", mensagem=" + e.getMessage());
        }
    }


}
