package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.payment.exception.InvalidPaymentStatusException;
import com.example.cinema.api.domain.payment.exception.InvalidTransactionIdException;
import com.example.cinema.api.domain.payment.exception.TransactionAlreadyRegisteredException;
import com.example.cinema.api.domain.payment.exception.PaymentMethodRequiredException;
import com.example.cinema.api.domain.payment.exception.PurchaseRequiredException;
import com.example.cinema.api.domain.purchase.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDateTime paymentDate;

    private Long transactionId;

    private int version;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentType paymentMethod;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;

    private String statusDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false, unique = true)
    @ToString.Exclude
    private Purchase purchase;

    public Payment(Purchase purchase, PaymentType paymentMethod) {

        if (purchase == null) {
            throw new PurchaseRequiredException("A compra é obrigatória para criar um pagamento.");
        }

        if (paymentMethod == null) {
            throw new PaymentMethodRequiredException("O método de pagamento é obrigatório.");
        }

        this.purchase = purchase;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;

        purchase.attachPayment(this);
    }

    private void moveToStatus(PaymentStatus newStatus) {
        this.paymentStatus = this.paymentStatus.transitionTo(newStatus);
    }

    public void registerTransaction(Long transactionId) {

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("TransactionId inválido.");
        }

        if (this.transactionId != null)
            throw new TransactionAlreadyRegisteredException("TransactionId já registrado.");

        this.transactionId = transactionId;
    }

    public boolean isOutdatedVersion(int eventVersion) {
        return eventVersion < this.version;
    }

    public void updateVersion(int newVersion) {
        this.version = newVersion;
    }

    public void updateStatus(PaymentStatus newStatus, String statusDetail) {

        if (newStatus == null) {
            throw new InvalidPaymentStatusException("O status do pagamento é obrigatório.");
        }

        moveToStatus(newStatus);
        this.statusDetail = statusDetail;
    }
}