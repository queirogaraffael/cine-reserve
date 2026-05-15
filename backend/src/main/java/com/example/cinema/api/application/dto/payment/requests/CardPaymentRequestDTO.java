package com.example.cinema.api.application.dto.payment.requests;

import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.dto.payment.PaymentAddressDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentRequestDTO implements PaymentRequestDTO {

    @NotBlank(message = "O identificador do método de pagamento é obrigatório")
    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    @NotBlank(message = "O token do cartão é obrigatório")
    @JsonProperty("card_token")
    private String cardToken;

    @NotNull(message = "O número de parcelas é obrigatório")
    @Min(value = 1, message = "O número mínimo de parcelas é 1")
    @Max(value = 12, message = "O número máximo de parcelas é 12")
    private Integer installments;

    @NotNull
    private PaymentAddressDTO paymentAddressDTO;

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CARD;
    }
}