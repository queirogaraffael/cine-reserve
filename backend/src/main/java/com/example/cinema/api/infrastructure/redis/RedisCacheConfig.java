package com.example.cinema.api.infrastructure.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
@Configuration
@Profile("!test")
public class RedisCacheConfig {

    @Value("${cache.ttl.genres}")
    private long genreTtl;

    @Value("${cache.ttl.movies}")
    private long movieTtl;

    @Value("${cache.ttl.rooms}")
    private long roomTtl;

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(
            GenericJackson2JsonRedisSerializer serializer) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory, RedisCacheConfiguration defaultConfig) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("genres", defaultConfig.entryTtl(Duration.ofSeconds(genreTtl)));

        cacheConfigurations.put("movies", defaultConfig.entryTtl(Duration.ofSeconds(movieTtl)));

        cacheConfigurations.put("rooms", defaultConfig.entryTtl(Duration.ofSeconds(roomTtl)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

}