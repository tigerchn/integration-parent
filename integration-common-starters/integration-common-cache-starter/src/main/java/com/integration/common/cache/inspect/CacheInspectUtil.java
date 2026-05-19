package com.integration.common.cache.inspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 本地 Caffeine 缓存探查工具：列出已注册区域、采样条目（key + value 文本）、读取命中率等。
 * <p>
 * 依赖 Spring {@link CacheManager}；枚举条目内容需底层为 {@link CaffeineCache}。
 * 未访问过的 {@code cacheNames} 可能尚未创建（{@link CacheRegionSnapshot#initialized()} 为 false）。
 * <p>
 * 返回的 {@link CacheRegionSnapshot} 为调用时刻的快照，后续 {@code evict} / 过期不会自动更新，需重新调用本类方法。
 */
public final class CacheInspectUtil {

    /**
     * 每个缓存区域默认最多采样的条目数
     */
    public static final int DEFAULT_ENTRY_SAMPLE_LIMIT = 100;

    /**
     * 单条 value 文本默认最大长度，超出则截断
     */
    public static final int DEFAULT_VALUE_MAX_LENGTH = 1024;

    /**
     * 工具类，禁止实例化。
     */
    private CacheInspectUtil() {
    }

    /**
     * 探查 {@link CacheManager} 中所有已注册缓存区域，使用默认采样上限与 value 长度。
     *
     * @param cacheManager Spring 缓存管理器
     * @return 各区域快照列表（仅包含 {@link #resolveCacheNames} 返回的名称）
     */
    public static List<CacheRegionSnapshot> inspectAll(CacheManager cacheManager) {
        return inspectAll(cacheManager, DEFAULT_ENTRY_SAMPLE_LIMIT, DEFAULT_VALUE_MAX_LENGTH);
    }

    /**
     * 探查所有已注册缓存区域，可指定每区条目采样上限。
     *
     * @param cacheManager     Spring 缓存管理器
     * @param entrySampleLimit 每个区域最多采样的条目数
     * @return 各区域快照列表
     */
    public static List<CacheRegionSnapshot> inspectAll(CacheManager cacheManager, int entrySampleLimit) {
        return inspectAll(cacheManager, entrySampleLimit, DEFAULT_VALUE_MAX_LENGTH);
    }

    /**
     * 探查所有已注册缓存区域，可指定采样条数与 value 文本长度上限。
     *
     * @param cacheManager     Spring 缓存管理器
     * @param entrySampleLimit 每个区域最多采样的条目数；{@code <= 0} 时不采样条目（{@code entries} 为空）
     * @param valueMaxLength   单条 value 文本上限；{@code <= 0} 时不截断
     * @return 各区域快照列表；无已创建区域时返回空列表
     */
    public static List<CacheRegionSnapshot> inspectAll(CacheManager cacheManager, int entrySampleLimit, int valueMaxLength) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        List<CacheRegionSnapshot> snapshots = new ArrayList<>();
        for (String name : resolveCacheNames(cacheManager)) {
            inspectRegion(cacheManager, name, entrySampleLimit, valueMaxLength).ifPresent(snapshots::add);
        }
        return List.copyOf(snapshots);
    }

    /**
     * 探查单个缓存区域，使用默认采样与 value 长度上限。
     *
     * @param cacheManager Spring 缓存管理器
     * @param cacheName    区域名，与 {@code @Cacheable(cacheNames)} 一致
     * @return 区域快照；区域尚未创建时 {@code initialized=false}
     */
    public static Optional<CacheRegionSnapshot> inspectRegion(CacheManager cacheManager, String cacheName) {
        return inspectRegion(cacheManager, cacheName, DEFAULT_ENTRY_SAMPLE_LIMIT, DEFAULT_VALUE_MAX_LENGTH);
    }

    /**
     * 探查单个缓存区域，可指定条目采样上限。
     *
     * @param cacheManager     Spring 缓存管理器
     * @param cacheName        区域名
     * @param entrySampleLimit 最多采样的条目数
     * @return 区域快照
     */
    public static Optional<CacheRegionSnapshot> inspectRegion(
            CacheManager cacheManager, String cacheName, int entrySampleLimit) {
        return inspectRegion(cacheManager, cacheName, entrySampleLimit, DEFAULT_VALUE_MAX_LENGTH);
    }

    /**
     * 探查单个缓存区域，返回条目采样、估算大小与 Caffeine 统计信息。
     *
     * @param cacheManager     Spring 缓存管理器
     * @param cacheName        区域名，不可为 blank
     * @param entrySampleLimit 最多采样的条目数；{@code <= 0} 时不采样
     * @param valueMaxLength   单条 value 文本上限；{@code <= 0} 时不截断
     * @return 区域快照；底层非 {@link CaffeineCache} 时 {@code estimatedSize} 为 {@code -1} 且 {@code entries} 为空
     * @throws IllegalArgumentException {@code cacheName} 为 null 或空白
     */
    public static Optional<CacheRegionSnapshot> inspectRegion(CacheManager cacheManager, String cacheName, int entrySampleLimit, int valueMaxLength) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName must not be blank");
        }
        org.springframework.cache.Cache cache = resolveCache(cacheManager, cacheName);
        if (cache == null) {
            return Optional.of(new CacheRegionSnapshot(cacheName, false, "not-created", 0L, List.of(), false, null));
        }
        return Optional.of(buildSnapshot(cache, cacheName, entrySampleLimit, valueMaxLength));
    }

    /**
     * 读取指定区域下单条缓存的快照，使用默认 value 文本长度上限。
     *
     * @param cacheManager Spring 缓存管理器
     * @param cacheName    区域名
     * @param key          缓存键，与 {@code @Cacheable(key)} 解析结果一致
     * @return 条目快照；区域未创建或 key 不存在时为空
     */
    public static Optional<CacheEntrySnapshot> getEntry(
            CacheManager cacheManager, String cacheName, Object key) {
        return getEntry(cacheManager, cacheName, key, DEFAULT_VALUE_MAX_LENGTH);
    }

    /**
     * 读取指定区域下单条缓存的快照（key + value 文本 + 类型名）。
     *
     * @param cacheManager   Spring 缓存管理器
     * @param cacheName      区域名，不可为 blank
     * @param key            缓存键
     * @param valueMaxLength value 文本最大长度；{@code <= 0} 时不截断
     * @return 条目快照；未命中时为空
     * @throws IllegalArgumentException {@code cacheName} 为 null 或空白
     */
    public static Optional<CacheEntrySnapshot> getEntry(
            CacheManager cacheManager, String cacheName, Object key, int valueMaxLength) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName must not be blank");
        }
        org.springframework.cache.Cache cache = resolveCache(cacheManager, cacheName);
        if (cache == null) {
            return Optional.empty();
        }
        Optional<Cache<Object, Object>> nativeCache = unwrapCaffeine(cache);
        if (nativeCache.isPresent()) {
            Object value = nativeCache.get().asMap().get(key);
            if (value == null && !nativeCache.get().asMap().containsKey(key)) {
                return Optional.empty();
            }
            return Optional.of(toEntrySnapshot(formatKey(key), value, valueMaxLength));
        }
        ValueWrapper wrapper = cache.get(key);
        if (wrapper == null) {
            return Optional.empty();
        }
        return Optional.of(toEntrySnapshot(formatKey(key), wrapper.get(), valueMaxLength));
    }

    /**
     * 返回 {@link CacheManager} 中已创建（已注册）的缓存区域名称列表。
     * <p>
     * 等价于 {@link CacheManager#getCacheNames()} 的不可变拷贝；未触发过 {@code @Cacheable} 的区域不会出现。
     *
     * @param cacheManager Spring 缓存管理器
     * @return 区域名称列表，不会为 null
     */
    public static List<String> resolveCacheNames(CacheManager cacheManager) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        return List.copyOf(cacheManager.getCacheNames());
    }

    /**
     * 判断指定区域中是否存在给定 key 的缓存条目。
     *
     * @param cacheManager Spring 缓存管理器
     * @param cacheName    区域名
     * @param key          缓存键
     * @return 存在且可读时为 {@code true}
     */
    public static boolean containsKey(CacheManager cacheManager, String cacheName, Object key) {
        return getEntry(cacheManager, cacheName, key).isPresent();
    }

    /**
     * 统计所有已注册缓存区域中的条目总数。
     * <p>
     * 对各区域 {@link #countEntries(CacheManager, String)} 求和；仅统计已惰性创建的区域。
     *
     * @param cacheManager Spring 缓存管理器
     * @return 条目总数；无已创建区域时为 {@code 0}
     */
    public static long countAllEntries(CacheManager cacheManager) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        long total = 0L;
        for (String name : resolveCacheNames(cacheManager)) {
            total += countEntries(cacheManager, name);
        }
        return total;
    }

    /**
     * 统计单个缓存区域中的条目数。
     * <p>
     * 底层为 {@link CaffeineCache} 时使用 {@link Cache#estimatedSize()}；区域未创建时返回 {@code 0}；
     * 非 Caffeine 实现暂无法统计，返回 {@code 0}。
     *
     * @param cacheManager Spring 缓存管理器
     * @param cacheName    区域名，不可为 blank
     * @return 该区域条目数
     * @throws IllegalArgumentException {@code cacheName} 为 null 或空白
     */
    public static long countEntries(CacheManager cacheManager, String cacheName) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName must not be blank");
        }
        org.springframework.cache.Cache cache = resolveCache(cacheManager, cacheName);
        if (cache == null) {
            return 0L;
        }
        return unwrapCaffeine(cache).map(Cache::estimatedSize).orElse(0L);
    }

    /**
     * 根据 Spring {@link org.springframework.cache.Cache} 构建区域快照。
     *
     * @param cache            已存在的缓存实例
     * @param cacheName        区域名
     * @param entrySampleLimit 条目采样上限
     * @param valueMaxLength   value 文本长度上限
     * @return 区域快照
     */
    private static CacheRegionSnapshot buildSnapshot(org.springframework.cache.Cache cache, String cacheName, int entrySampleLimit, int valueMaxLength) {
        String implType = cache.getClass().getSimpleName();
        Optional<Cache<Object, Object>> nativeCache = unwrapCaffeine(cache);
        if (nativeCache.isEmpty()) {
            return new CacheRegionSnapshot(cacheName, true, implType, -1L, List.of(), false, null);
        }
        Cache<Object, Object> caffeine = nativeCache.get();
        long estimatedSize = caffeine.estimatedSize();
        EntrySample sample = sampleEntries(caffeine, entrySampleLimit, valueMaxLength);
        return new CacheRegionSnapshot(
                cacheName,
                true,
                implType,
                estimatedSize,
                sample.entries(),
                sample.truncated(),
                toStatsSnapshot(caffeine));
    }

    /**
     * 从 Caffeine 原生缓存中采样条目，最多 {@code limit} 条。
     *
     * @param caffeine       Caffeine 原生缓存
     * @param limit          最大采样条数；{@code <= 0} 时返回空列表
     * @param valueMaxLength value 文本截断长度
     * @return 采样结果及是否因上限被截断
     */
    private static EntrySample sampleEntries(Cache<Object, Object> caffeine, int limit, int valueMaxLength) {
        if (limit <= 0) {
            return new EntrySample(List.of(), false);
        }
        List<CacheEntrySnapshot> entries = new ArrayList<>();
        boolean truncated = false;
        for (Map.Entry<Object, Object> entry : caffeine.asMap().entrySet()) {
            if (entries.size() >= limit) {
                truncated = true;
                break;
            }
            entries.add(toEntrySnapshot(formatKey(entry.getKey()), entry.getValue(), valueMaxLength));
        }
        if (!truncated && caffeine.estimatedSize() > entries.size()) {
            truncated = true;
        }
        return new EntrySample(List.copyOf(entries), truncated);
    }

    /**
     * 将 key/value 格式化为 {@link CacheEntrySnapshot}。
     *
     * @param key            已格式化的 key 文本
     * @param value          缓存值，可为 null
     * @param valueMaxLength value 文本截断长度
     * @return 条目快照
     */
    private static CacheEntrySnapshot toEntrySnapshot(String key, @Nullable Object value, int valueMaxLength) {
        FormattedValue formatted = formatValue(value, valueMaxLength);
        return new CacheEntrySnapshot(key, formatted.text(), formatted.typeName(), formatted.truncated());
    }

    /**
     * 将缓存键转为可读字符串。
     *
     * @param key 原始 key，可为 null
     * @return {@code "null"} 或 {@link String#valueOf(Object)}
     */
    private static String formatKey(Object key) {
        return key == null ? "null" : String.valueOf(key);
    }

    /**
     * 将缓存值转为展示用文本与类型名，并按长度截断。
     *
     * @param value          缓存值，可为 null
     * @param valueMaxLength 最大文本长度；{@code <= 0} 不截断
     * @return 格式化结果
     */
    private static FormattedValue formatValue(@Nullable Object value, int valueMaxLength) {
        if (value == null) {
            return new FormattedValue("null", "null", false);
        }
        String typeName = value.getClass().getName();
        String text = String.valueOf(value);
        if (valueMaxLength > 0 && text.length() > valueMaxLength) {
            return new FormattedValue(text.substring(0, valueMaxLength) + "…", typeName, true);
        }
        return new FormattedValue(text, typeName, false);
    }

    /**
     * 读取 Caffeine {@link CacheStats} 并转为 {@link CacheStatsSnapshot}。
     *
     * @param caffeine 已开启 {@code recordStats} 的 Caffeine 缓存
     * @return 统计快照；stats 不可用时为 null
     */
    @Nullable
    private static CacheStatsSnapshot toStatsSnapshot(Cache<Object, Object> caffeine) {
        CacheStats stats = caffeine.stats();
        if (stats == null) {
            return null;
        }
        return new CacheStatsSnapshot(
                stats.requestCount(),
                stats.hitCount(),
                stats.missCount(),
                stats.hitRate(),
                stats.loadSuccessCount(),
                stats.loadFailureCount(),
                stats.totalLoadTime(),
                stats.evictionCount());
    }

    /**
     * 从 {@link CacheManager} 解析已创建的 {@link org.springframework.cache.Cache}。
     *
     * @param cacheManager Spring 缓存管理器
     * @param cacheName    区域名
     * @return 缓存实例；尚未惰性创建时为 null
     */
    @Nullable
    private static org.springframework.cache.Cache resolveCache(CacheManager cacheManager, String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    /**
     * 若 Spring Cache 包装为 {@link CaffeineCache}，则返回底层 Caffeine 原生缓存。
     *
     * @param cache Spring 缓存抽象
     * @return 原生 Caffeine 缓存；非 Caffeine 实现时为空
     */
    private static Optional<Cache<Object, Object>> unwrapCaffeine(org.springframework.cache.Cache cache) {
        if (cache instanceof CaffeineCache caffeineCache) {
            return Optional.of(caffeineCache.getNativeCache());
        }
        return Optional.empty();
    }

    /**
     * 区域条目采样结果：条目列表 + 是否被截断
     */
    private record EntrySample(List<CacheEntrySnapshot> entries, boolean truncated) {
    }

    /**
     * value 格式化中间结果：展示文本、类型名、是否截断
     */
    private record FormattedValue(String text, String typeName, boolean truncated) {
    }
}
