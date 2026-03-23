package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.application.dto.webhook.ExternalPaymentSnapshot;
import com.example.cinema.api.application.dto.webhook.PaymentWebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentUpdateService {

    private final PaymentRepositoryJpa paymentRepositoryJpa;

    public PaymentUpdateService(PaymentRepositoryJpa paymentRepositoryJpa) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
    }

    @Transactional
    public void processPaymentUpdate(ExternalPaymentSnapshot externalPaymentSnapshot, PaymentWebhookEvent event) {

        if (externalPaymentSnapshot.externalReference() == null) {
            throw new InvalidPaymentSnapshotException("Pagamento sem external_reference");
        }

        Long purchaseId = externalPaymentSnapshot.externalReference();

        // 1 - Manter findById atual de Payment
        // 2 - Método de find Purchase
        // 3 -

        // pega a compra junto com o payment -> quem pode salvar o outro


        // verifica se  fazer tratamento especial para cada -> o tratamento tem que partitir do status do payment -> dai você modifica
        // para alguns (alguns cria ate uns grupos) para alterar o de purchase


        //CREATED("created"),
        //    WAITING_PAYMENT("waiting_payment"),
        // CONFIRMED("confirmed"),
        //    REFUNDED("refunded"), -> deveria retirar, pois isso esta mais associado a pagamento do que a purchase
        //    CANCELLED("cancelled"), -> so é cancelado se o pagamento for cancelado ou se não houver pagamento -> o schedule deve partir desse
        //    EXPIRED("expired"); -> igual refund

        // criar um schedule para liberar compras (tickets) que nao tiveram pagamento efetuados
        // um metodo que libere o assento -> numero do assento + id da sessão
        // retornar uma compra deveria vir com o status do payment(o status de pedido deve ser apenas para fins de validação


        // qais implicações eu vou ter se eu resertar os tickets ? sera que vai ter algum problema para outros usuarios reservarem ou comprarem ?


        var paymentLocal = paymentRepositoryJpa.findByPurchaseId(purchaseId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Pagamento não encontrado para o PurchaseId: " + purchaseId));

        if (paymentLocal.isOutdatedVersion(event.getVersion())) {
            log.info("Evento desatualizado ignorado para o pagamento {}", purchaseId);
            return;
        }

        paymentLocal.updateVersion(event.getVersion());

        PaymentStatus novoStatus = PaymentStatus.fromValue(externalPaymentSnapshot.status());

        if (paymentLocal.getPaymentStatus() == novoStatus) {
            log.info("Pagamento {} já está no status {}", purchaseId, novoStatus);
            return;
        }

        // TODO: fazer modificação do status de Purchase -> purchase ser liberada

        paymentLocal.updateStatus(novoStatus, externalPaymentSnapshot.statusDetail());

        paymentRepositoryJpa.save(paymentLocal);
    }
}

