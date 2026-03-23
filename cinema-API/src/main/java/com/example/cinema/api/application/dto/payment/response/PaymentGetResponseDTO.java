package com.example.cinema.api.application.dto.payment.response;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGetResponseDTO {
    Long id;
    LocalDateTime paymentDate;
    Long transactionId;
    PaymentType paymentMethod;
    PaymentStatus paymentStatus;
    String statusDetail;
    Long purchaseId;
}
