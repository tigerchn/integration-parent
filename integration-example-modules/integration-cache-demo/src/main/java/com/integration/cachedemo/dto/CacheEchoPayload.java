package com.integration.cachedemo.dto;

import java.time.Instant;

/**
 * 缓存命中时两次响应体应完全一致（含 {@link #computedAt} 与 {@link #missSequence}）。
 */
public record CacheEchoPayload(String key, String computedAt, long missSequence, long simulatedCostMs) {}
