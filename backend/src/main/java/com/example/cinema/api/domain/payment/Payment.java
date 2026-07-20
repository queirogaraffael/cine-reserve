package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.payment.exception.InvalidPaymentStatusException;
import com.example.cinema.api.domain.payment.exception.InvalidTransactionIdException;
import com.example.cinema.api.domain.payment.exception.TransactionAlreadyRegisteredException;
import com.example.cinema.api.domain.payment.exception.PaymentMethodRequiredException;
import com.example.cinema.api.domain.order.Order;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Version
    private Long version;

    private LocalDateTime paymentDate;

    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentType paymentMethod;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;

    private String statusDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @ToString.Exclude
    private Order order;

    public Payment(Order order, PaymentType paymentMethod) {

        if (order == null) {
            throw new IllegalArgumentException("O pedido é obrigatório para criar um pagamento.");
        }

        if (paymentMethod == null) {
            throw new PaymentMethodRequiredException("O método de pagamento é obrigatório.");
        }

        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
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

    public void updateStatus(PaymentStatus newStatus, String statusDetail) {
        if (newStatus == null || newStatus == PaymentStatus.UNKNOWN) {
            throw new InvalidPaymentStatusException("Status invalido: " + newStatus);
        }
        moveToStatus(newStatus);
        this.statusDetail = statusDetail;
    }
}