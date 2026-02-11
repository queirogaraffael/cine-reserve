package com.example.cinema.api.domain.payment;

import com.example.cinema.api.domain.purchase.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime paymentDate;

    private Long transactionId;

    private int version;

    @Enumerated
    @NotNull
    private PaymentType paymentMethod;

    @Enumerated
    @NotNull
    private PaymentStatus paymentStatus;

    private String statusDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", unique = true)
    private Purchase purchase;

}
