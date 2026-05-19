package com.integration.cachedemo.service;

import com.integration.cachedemo.dto.CacheEchoPayload;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class CacheDevService {

    public static final String CACHE_NAME = "cache_dev";

    private static final long SIMULATED_COST_MS = 1000L;

    private final AtomicLong missSequence = new AtomicLong();

    @Cacheable(cacheNames = CACHE_NAME, key = "#key")
    public CacheEchoPayload echo(String key) {
        try {
            Thread.sleep(SIMULATED_COST_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted during simulated load", e);
        }

        Instant instant = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatTime = instant.atZone(ZoneId.of("Asia/Shanghai"))
                .format(formatter);
        return new CacheEchoPayload(key, formatTime, missSequence.incrementAndGet(), SIMULATED_COST_MS);
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#key")
    public void evict(String key) {
        // no-op
    }

    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public void clearAll() {
        // no-op
    }
}
