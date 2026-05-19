package com.integration.cachedemo.dto;

/**
 * 指定 key 在演示缓存区域中是否存在。
 */
public record CacheKeyPresence(String cacheName, String key, boolean cached) {}
