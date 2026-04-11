package com.example.cinema.api.infrastructure.caching;

import com.example.cinema.api.shared.IntegrationTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("integration")
class RedisCacheIT extends IntegrationTestBase {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void shouldStoreAndRetrieveSession() {
        redisTemplate.opsForValue().set("session:123", "payload");
        var result = redisTemplate.opsForValue().get("session:123");

        assertThat(result).isEqualTo("payload");
    }
}