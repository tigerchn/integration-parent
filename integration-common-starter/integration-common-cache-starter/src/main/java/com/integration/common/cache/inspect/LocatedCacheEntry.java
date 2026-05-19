package com.integration.common.cache.inspect;

/**
 * 在指定 Spring Cache 区域中命中的条目。
 */
public record LocatedCacheEntry(String cacheName, CacheEntrySnapshot entry) {
}
