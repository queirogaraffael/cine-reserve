package com.example.cinema.api.infrastructure.mercadopago.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MercadoPagoWebhookNotificationDTO implements WebhookPayloadDTO {
    private String id;
    private String type;
    private String topic;
    private String resource;
    private JsonNode data;

    public Long getResourceId() {
        if (data != null && data.has("id")) {
            return data.get("id").asLong();
        }
        return null;
    }
}
