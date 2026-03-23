package com.example.cinema.api.infrastructure.mercadopago.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MercadoPagoWebhookDTO {

    private String type;
    private String action;
    private PaymentData data;
    private int version;

    @JsonProperty("live_mode")
    private boolean liveMode;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentData {
        private Long id;
    }
}