package com.example.cinema.api.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Profile("test")
public class TestRedisConfig {

    @Bean
    @Primary
    @Qualifier("userSessionRedisTemplate")
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, String> userSessionRedisTemplate() {
        return Mockito.mock(RedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
    }
}
