package com.example.cinema.api.configs;

import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class PaymentConfig {

    @Bean
    public Map<PaymentType, Class<? extends PaymentRequestDTO>> paymentRequestDTOMap() {
        Map<PaymentType, Class<? extends PaymentRequestDTO>> map = new EnumMap<>(PaymentType.class);

        map.put(PaymentType.CARD, CardPaymentRequestDTO.class);
        map.put(PaymentType.PIX, PixPaymentRequestDTO.class);
        return map;
    }
}