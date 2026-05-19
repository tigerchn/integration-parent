package com.integration.cachedemo.service;

import com.integration.cachedemo.dto.CacheEchoPayload;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class CacheDemoService {

    /**
     * 与 {@link org.springframework.cache.annotation.Cacheable#cacheNames} 一致，供探查接口使用
     */
    public static final String CACHE_NAME = "demoEcho";

    private static final long SIMULATED_COST_MS = 60L;

    private final AtomicLong missSequence = new AtomicLong();

    /**
     * 未命中缓存时会休眠一段时间；命中缓存时不会进入方法体。
     *
     * @param key 缓存键
     * @return 负载（命中时返回历史上第一次计算的结果）
     */
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
