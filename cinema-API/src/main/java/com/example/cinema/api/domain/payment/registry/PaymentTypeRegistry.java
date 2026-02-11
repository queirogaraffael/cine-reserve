package com.example.cinema.api.domain.payment.registry;

import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class PaymentTypeRegistry {

    @Bean
    public Map<PaymentType, Class<? extends PaymentRequestDTO>> paymentRequestDTOMap() {
        Map<PaymentType, Class<? extends PaymentRequestDTO>> map = new EnumMap<>(PaymentType.class);

        map.put(PaymentType.CARD, CardPaymentRequestDTO.class);
        map.put(PaymentType.PIX, PixPaymentRequestDTO.class);
        return map;
    }
}