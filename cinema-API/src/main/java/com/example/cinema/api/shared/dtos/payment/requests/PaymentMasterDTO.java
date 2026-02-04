package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PaymentMasterDTO {

    private PaymentType paymentMethod;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "paymentMethod"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PixPaymentRequestDTO.class, name = "PIX"),
            @JsonSubTypes.Type(value = CardPaymentRequestDTO.class, name = "CARD")
    })
    private PaymentRequestDTO paymentDetails;
}