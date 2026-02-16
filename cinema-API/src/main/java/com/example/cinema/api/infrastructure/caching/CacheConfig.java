package com.example.cinema.api.infrastructure.caching;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;

@Configuration
public class CacheConfig {

    @Value("${cache.ttl.standard:3600}")
    private long standardTtl;

    @Value("${cache.ttl.short:600}")
    private long shortTtl;

    @Bean
    @Primary
    @Qualifier("standardCache")
    public CacheControl standardCache() {
        return CacheControl.maxAge(standardTtl, TimeUnit.SECONDS).cachePublic();
    }

    @Bean
    @Qualifier("shortCache")
    public CacheControl shortCache() {
        return CacheControl.maxAge(shortTtl, TimeUnit.SECONDS).mustRevalidate();
    }

    @Bean
    @Qualifier("shortCachePrivate")
    public CacheControl shortCachePrivate() {
        return CacheControl.maxAge(shortTtl, TimeUnit.SECONDS).cachePrivate();
    }

    @Bean
    @Qualifier("noCachePrivate")
    public CacheControl noCachePrivate() {
        return CacheControl.noStore().cachePrivate();
    }
}