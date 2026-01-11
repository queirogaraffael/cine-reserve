package com.example.cinema.api.shared.dtos.payment.response.gateway.card;

import com.example.cinema.api.shared.dtos.payment.response.gateway.PaymentGatewayResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardGatewayResult implements PaymentGatewayResponseDTO {
    private Long transactionId;
    private String status;
    private String statusDetail;
    private String lastFourDigits;
    private Integer installments;
    private String paymentMethodId;
}
