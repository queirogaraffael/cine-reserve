package com.example.cinema.api.application.dto.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentRequestDTO implements PaymentRequestDTO {

    @JsonProperty("card_token")
    private String cardToken;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    private Integer installments;

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CARD;
    }
}
