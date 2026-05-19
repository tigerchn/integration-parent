package com.integration.cachedemo.dto;

import com.integration.common.cache.inspect.CacheEntrySnapshot;

/**
 * 单条本地缓存探查结果（含快照与反序列化后的业务对象）。
 */
public record CacheKeyDetail(
        String cacheName,
        String key,
        boolean cached,
        CacheEntrySnapshot snapshot,
        CacheEchoPayload value) {}
