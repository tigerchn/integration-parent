package com.integration.common.cache.inspect;

import java.util.List;

/**
 * 单个 Spring Cache 区域（如 {@code demoEcho}）的探查结果。
 */
public record CacheRegionSnapshot(
        String name,
        boolean initialized,
        String implementationType,
        long estimatedSize,
        List<CacheEntrySnapshot> entries,
        boolean entriesTruncated,
        CacheStatsSnapshot stats) {
}
