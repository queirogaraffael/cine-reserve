package com.example.cinema.api.shared.dtos.payment.requests;

import com.example.cinema.api.domain.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMasterDTO {
    private PaymentType paymentMethod;
    private Object paymentDetails;
}