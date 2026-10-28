package com.example.cinema.api.infrastructure.mercadopago.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.http.connection-timeout-ms:5000}")
    private int connectionTimeout;

    @Value("${mercadopago.http.socket-timeout-ms:10000}")
    private int socketTimeout;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        MercadoPagoConfig.setConnectionTimeout(connectionTimeout);
        MercadoPagoConfig.setSocketTimeout(socketTimeout);
        log.info("Mercado Pago SDK configurado com sucesso. ConnectionTimeout={}, SocketTimeout={}", connectionTimeout, socketTimeout);
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
