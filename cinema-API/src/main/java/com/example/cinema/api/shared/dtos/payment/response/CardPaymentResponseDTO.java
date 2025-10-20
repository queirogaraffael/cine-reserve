package com.example.cinema.api.shared.dtos.payment.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponseDTO implements PaymentResponseDTO{

    private Long transactionId;
    private String paymentStatus;
    private String lastFourDigits;
    private Integer installments;
    private String paymentMethodId;
}
