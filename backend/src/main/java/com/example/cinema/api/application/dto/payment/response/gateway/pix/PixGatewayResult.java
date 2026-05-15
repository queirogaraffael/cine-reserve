package com.example.cinema.api.application.dto.payment.response.gateway.pix;

import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixGatewayResult implements PaymentGatewayResponseDTO {

    private Long transactionId;
    private String status;
    private String statusDetail;
    private String pixCopiaECola;
    private String qrCodeBase64;
    private String instrucoesUrl;
    private ZonedDateTime expirationDate;
}