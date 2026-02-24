package com.example.cinema.api.application.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentCardInitiatedNotificationData {

    private Long idPayment;
    private String name;
    private String email;
    private LocalDateTime paymentDate;
    private BigDecimal totalPrice;
}
