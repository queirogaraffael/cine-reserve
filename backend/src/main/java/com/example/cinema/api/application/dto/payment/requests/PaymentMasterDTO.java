package com.example.cinema.api.application.dto.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentMasterDTO {

        private PaymentType paymentMethod;

        @NotBlank(message = "A chave de idempotência é obrigatória")
        @Size(max = 64, message = "A chave de idempotência deve ter no máximo 64 caracteres")
        private String idempotencyKey;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "paymentMethod")
        @JsonSubTypes({
                        @JsonSubTypes.Type(value = PixPaymentRequestDTO.class, name = "PIX"),
                        @JsonSubTypes.Type(value = CardPaymentRequestDTO.class, name = "CARD")
        })
        private PaymentRequestDTO paymentDetails;
}