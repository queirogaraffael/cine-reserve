package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.payment.exception.InvalidPaymentStatusException;
import com.example.cinema.api.domain.payment.exception.InvalidTransactionIdException;
import com.example.cinema.api.domain.payment.exception.TransactionAlreadyRegisteredException;
import com.example.cinema.api.domain.payment.exception.PaymentMethodRequiredException;
import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.domain.payment.exception.OrderRequiredException;
import com.example.cinema.api.domain.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "order_payment")
public class OrderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Version
    private Long version;

    private LocalDateTime paymentDate;

    private String providerPaymentId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentType paymentMethod;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;

    private String statusDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    @OneToMany(mappedBy = "orderPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PaymentTransaction> transactions = new ArrayList<>();

    public OrderPayment(Order order, PaymentType paymentMethod, String idempotencyKey) {

        if (order == null) {
            throw new OrderRequiredException("O pedido é obrigatório para criar um pagamento.");
        }

        if (paymentMethod == null) {
            throw new PaymentMethodRequiredException("O método de pagamento é obrigatório.");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new PaymentValidationException("A chave de idempotência é obrigatória para criar um pagamento.");
        }

        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
        this.idempotencyKey = idempotencyKey;
    }

    private void moveToStatus(PaymentStatus newStatus) {
        this.paymentStatus = this.paymentStatus.transitionTo(newStatus);
    }

    public void setProviderPaymentId(String providerPaymentId) {

        if (providerPaymentId == null || providerPaymentId.isBlank()) {
            throw new InvalidTransactionIdException("providerPaymentId inválido.");
        }

        if (this.providerPaymentId != null)
            throw new TransactionAlreadyRegisteredException("providerPaymentId já registrado.");

        this.providerPaymentId = providerPaymentId;
    }

    public void registerTransaction(PaymentStatus newStatus, String source, String details,
            LocalDateTime gatewayTimestamp) {
        if (newStatus == null || newStatus == PaymentStatus.UNKNOWN) {
            throw new InvalidPaymentStatusException("Status invalido: " + newStatus);
        }

        moveToStatus(newStatus);
        this.statusDetail = details;

        this.transactions.add(new PaymentTransaction(this, newStatus, source, details, gatewayTimestamp));
    }
}