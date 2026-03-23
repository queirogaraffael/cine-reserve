package com.example.cinema.api.domain.payment.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentCardInitiatedEvent {

    private Long idPayment;
    private UUID userId;
    private LocalDateTime paymentDate;
    private BigDecimal totalPrice;
}
