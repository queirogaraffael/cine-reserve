package com.example.cinema.api.domain.payment;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_payment_id", nullable = false)
    @ToString.Exclude
    private OrderPayment orderPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(length = 100)
    private String source;

    @Column(length = 255)
    private String details;

    private LocalDateTime gatewayTimestamp;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PaymentTransaction(OrderPayment orderPayment, PaymentStatus status, String source, String details, LocalDateTime gatewayTimestamp) {
        this.orderPayment = orderPayment;
        this.status = status;
        this.source = source;
        this.details = details;
        this.gatewayTimestamp = gatewayTimestamp;
        this.createdAt = LocalDateTime.now();
    }
}
