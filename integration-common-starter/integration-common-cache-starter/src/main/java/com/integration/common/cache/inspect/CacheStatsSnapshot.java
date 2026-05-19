package com.integration.common.cache.inspect;

/**
 * Caffeine {@code recordStats} 统计快照；未开启统计时各计数为 0。
 */
public record CacheStatsSnapshot(
        long requestCount,
        long hitCount,
        long missCount,
        double hitRate,
        long loadSuccessCount,
        long loadFailureCount,
        long totalLoadTimeNanos,
        long evictionCount) {
}
