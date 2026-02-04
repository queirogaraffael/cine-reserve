package com.example.cinema.api.domain.entities;

import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.enums.PaymentStatus;
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

    @Enumerated
    @NotNull
    private PaymentType paymentMethod;

    @Enumerated
    @NotNull
    private PaymentStatus paymentStatus;

    private String statusDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
