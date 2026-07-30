package com.example.cinema.api.application.dto.coupon;

import com.example.cinema.api.domain.coupon.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateDTO {

    private String code;

    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @NotNull(message = "O tipo de desconto é obrigatório")
    private DiscountType discountType;

    @NotNull(message = "O valor do desconto é obrigatório")
    private BigDecimal discountValue;

    private LocalDateTime expirationDate;

    private UUID userId;

    private Long cinemaId;
}
