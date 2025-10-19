package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentBoletoRequestDTO implements PaymentRequestDTO{

    private String zipCode;
    private String streetName;
    private String streetNumber;
    private String neighborhood;
    private String cityName;
    private String federalUnit;

    @Override
    public PaymentType getPaymentMethod() {
        return PaymentType.BOLETO;
    }
}
