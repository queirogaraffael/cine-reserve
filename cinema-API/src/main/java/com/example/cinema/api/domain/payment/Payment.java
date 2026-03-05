package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.payment.exception.PaymentMethodRequiredException;
import com.example.cinema.api.domain.payment.exception.PurchaseRequiredException;
import com.example.cinema.api.domain.purchase.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    @JoinColumn(name = "purchase_id", unique = true)
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
    }

}
