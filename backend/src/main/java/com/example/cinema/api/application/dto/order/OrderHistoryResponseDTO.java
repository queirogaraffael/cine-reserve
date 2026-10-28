package com.example.cinema.api.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponseDTO {
    private Long orderId;
    private String exhibitionTitle;
    private String movieImageUrl;
    private LocalDateTime sessionDate;
    private LocalDateTime reservedAt;
    private String seats;
    private BigDecimal total;
    private String paymentStatus;
    private boolean isCancelled;
    private boolean canRetryPayment;
}
