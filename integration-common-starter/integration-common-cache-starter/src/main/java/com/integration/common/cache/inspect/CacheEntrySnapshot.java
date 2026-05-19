package com.integration.common.cache.inspect;

/**
 * 单条本地缓存条目快照（key + value 文本表示）。
 */
public record CacheEntrySnapshot(String key, String value, String valueType, boolean valueTruncated) {
}
