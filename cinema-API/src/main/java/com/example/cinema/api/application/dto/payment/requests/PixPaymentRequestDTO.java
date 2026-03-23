package com.example.cinema.api.application.dto.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PixPaymentRequestDTO implements PaymentRequestDTO {

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.PIX;
    }
}
