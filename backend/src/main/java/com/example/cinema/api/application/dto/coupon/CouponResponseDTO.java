package com.example.cinema.api.application.dto.coupon;

import com.example.cinema.api.domain.coupon.CouponStatus;
import com.example.cinema.api.domain.coupon.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CouponResponseDTO {
    private Long id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private UUID userId;
    private Long cinemaId;
    private CouponStatus status;
    private boolean active;
}
