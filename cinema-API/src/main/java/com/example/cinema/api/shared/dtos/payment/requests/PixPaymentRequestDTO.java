package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PixPaymentRequestDTO implements PaymentRequestDTO {

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.PIX;
    }
}
