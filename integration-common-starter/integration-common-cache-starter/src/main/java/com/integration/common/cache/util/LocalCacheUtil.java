package com.integration.common.cache.util;

import com.integration.common.cache.inspect.CacheEntrySnapshot;
import com.integration.common.cache.inspect.CacheInspectUtil;
import com.integration.common.cache.inspect.LocatedCacheEntry;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 本地 Caffeine 缓存工具，用法类似 Redis 侧工具类：按 key 判断是否命中、读取条目内容。
 * <p>
 * 推荐在已知 {@code @Cacheable(cacheNames = ...)} 时使用 {@link #hasKey(String, Object)} /
 * {@link #getEntry(String, Object)}；仅持有业务 key 时可使用 {@link #hasKey(Object)} /
 * {@link #findEntry(Object)} 扫描全部已注册区域；统计条目总数见 {@link #totalCount()}。
 * 程序化写入/读取对应 {@code @Cacheable}：{@link #put(String, Object, Object)} /
 * {@link #getOrLoad(String, Object, Supplier)}；驱逐见 {@link #evict(String, Object)} /
 * {@link #clear(String)}、{@link #evict(Object)}、{@link #clearAll()}。
 * <p>
 * 生产环境慎用（value 可能含敏感数据）；建议 dev/test 或内网排障。
 */
public final class LocalCacheUtil {

    private final CacheManager cacheManager;

    public LocalCacheUtil(CacheManager cacheManager) {
        this.cacheManager = Objects.requireNonNull(cacheManager, "cacheManager");
    }

    /**
     * 指定缓存区域与 key，判断本地是否已有条目。
     *
     * @param cacheName 与 {@code @Cacheable(cacheNames)} 一致的区域名
     * @param cacheKey  与 {@code @Cacheable(key)} 解析后的键（如 SpEL {@code #key} 的值）
     */
    public boolean hasKey(String cacheName, Object cacheKey) {
        return CacheInspectUtil.containsKey(cacheManager, cacheName, cacheKey);
    }

    /**
     * 在全部已注册缓存区域中查找是否存在该 key（任一命中即 true）。
     */
    public boolean hasKey(Object cacheKey) {
        for (String cacheName : CacheInspectUtil.resolveCacheNames(cacheManager)) {
            if (hasKey(cacheName, cacheKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取指定区域下单条缓存的快照（含 value 文本与类型）。
     */
    public Optional<CacheEntrySnapshot> getEntry(String cacheName, Object cacheKey) {
        return CacheInspectUtil.getEntry(cacheManager, cacheName, cacheKey);
    }

    /**
     * 读取指定区域下的缓存值对象；未命中时为空。
     */
    public Optional<Object> get(String cacheName, Object cacheKey) {
        return unwrapValue(cacheName, cacheKey, null);
    }

    /**
     * 按期望类型读取；类型不匹配时返回空（不抛异常）。
     */
    public <T> Optional<T> get(String cacheName, Object cacheKey, Class<T> type) {
        return unwrapValue(cacheName, cacheKey, type);
    }

    /**
     * 扫描全部区域，返回首个命中的条目及其区域名。
     */
    public Optional<LocatedCacheEntry> findEntry(Object cacheKey) {
        for (String cacheName : CacheInspectUtil.resolveCacheNames(cacheManager)) {
            Optional<CacheEntrySnapshot> entry = getEntry(cacheName, cacheKey);
            if (entry.isPresent()) {
                return Optional.of(new LocatedCacheEntry(cacheName, entry.get()));
            }
        }
        return Optional.empty();
    }

    /**
     * 统计所有已注册本地缓存区域中的条目总数。
     * <p>
     * 仅包含 {@link CacheManager#getCacheNames()} 中已创建的区域；未触发过 {@code @Cacheable} 的区域不计入。
     *
     * @return 各区域条目数之和
     */
    public long totalCount() {
        return CacheInspectUtil.countAllEntries(cacheManager);
    }

    /**
     * 统计指定缓存区域中的条目数。
     *
     * @param cacheName 与 {@code @Cacheable(cacheNames)} 一致的区域名
     * @return 该区域条目数；区域未创建时为 {@code 0}
     */
    public long count(String cacheName) {
        return CacheInspectUtil.countEntries(cacheManager, cacheName);
    }

    /**
     * 向指定区域写入条目，等价于方法返回后由 {@code @Cacheable} 落库的一次 put。
     * <p>
     * 区域尚未创建时不做任何操作。
     */
    public void put(String cacheName, Object cacheKey, @Nullable Object value) {
        org.springframework.cache.Cache cache = resolveCache(cacheName);
        if (cache != null) {
            cache.put(cacheKey, value);
        }
    }

    /**
     * 按 cache-aside 读取：命中则直接返回，未命中时调用 {@code loader} 计算并写入缓存后返回。
     * <p>
     * 等价于对 {@code @Cacheable(cacheNames, key)} 标注方法的程序化封装；{@code loader} 仅在未命中时执行。
     * 区域尚未创建时不会写入缓存，但仍会执行 {@code loader} 并返回其结果。
     *
     * @param loader 未命中时的值来源，不可为 null
     */
    public <T> T getOrLoad(String cacheName, Object cacheKey, Supplier<T> loader) {
        return getOrLoad(cacheName, cacheKey, null, loader);
    }

    /**
     * 按期望类型执行 cache-aside；命中但类型不匹配时视为未命中并重新加载。
     *
     * @param type   期望的 value 类型；为 null 时不校验类型
     * @param loader 未命中时的值来源，不可为 null
     */
    public <T> T getOrLoad(String cacheName, Object cacheKey, @Nullable Class<T> type, Supplier<T> loader) {
        Objects.requireNonNull(loader, "loader");
        org.springframework.cache.Cache cache = resolveCache(cacheName);
        if (cache == null) {
            return loader.get();
        }
        org.springframework.cache.Cache.ValueWrapper wrapper = cache.get(cacheKey);
        if (wrapper != null) {
            Object value = wrapper.get();
            if (matchesType(value, type)) {
                return castValue(value, type);
            }
        }
        T loaded = loader.get();
        if (loaded != null) {
            cache.put(cacheKey, loaded);
        }
        return loaded;
    }

    /**
     * 驱逐指定区域下的单条缓存，等价于 {@code @CacheEvict(cacheNames, key = ...)}。
     * <p>
     * 区域尚未创建时不做任何操作。
     *
     * @return 驱逐前该 key 是否存在
     */
    public boolean evict(String cacheName, Object cacheKey) {
        org.springframework.cache.Cache cache = resolveCache(cacheName);
        if (cache == null) {
            return false;
        }
        boolean existed = hasKey(cacheName, cacheKey);
        cache.evict(cacheKey);
        return existed;
    }

    /**
     * 清空指定缓存区域的全部条目，等价于 {@code @CacheEvict(cacheNames, allEntries = true)}。
     * <p>
     * 区域尚未创建时不做任何操作。
     */
    public void clear(String cacheName) {
        org.springframework.cache.Cache cache = resolveCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * 在全部已注册缓存区域中驱逐该 key（与 {@link #hasKey(Object)} 扫描范围一致）。
     *
     * @return 是否至少从一个区域中移除了已存在的条目
     */
    public boolean evict(Object cacheKey) {
        boolean removed = false;
        for (String cacheName : CacheInspectUtil.resolveCacheNames(cacheManager)) {
            if (evict(cacheName, cacheKey)) {
                removed = true;
            }
        }
        return removed;
    }

    /**
     * 清空所有已注册本地缓存区域，等价于对每个区域执行 {@link #clear(String)}。
     */
    public void clearAll() {
        for (String cacheName : CacheInspectUtil.resolveCacheNames(cacheManager)) {
            clear(cacheName);
        }
    }

    @Nullable
    private org.springframework.cache.Cache resolveCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    private static <T> boolean matchesType(@Nullable Object value, @Nullable Class<T> type) {
        return type == null || value == null || type.isInstance(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T castValue(@Nullable Object value, @Nullable Class<T> type) {
        return type == null ? (T) value : type.cast(value);
    }

    private <T> Optional<T> unwrapValue(String cacheName, Object cacheKey, @Nullable Class<T> type) {
        org.springframework.cache.Cache cache = resolveCache(cacheName);
        if (cache == null) {
            return Optional.empty();
        }
        org.springframework.cache.Cache.ValueWrapper wrapper = cache.get(cacheKey);
        if (wrapper == null) {
            return Optional.empty();
        }
        Object value = wrapper.get();
        if (type == null) {
            @SuppressWarnings("unchecked")
            T cast = (T) value;
            return Optional.ofNullable(cast);
        }
        if (value != null && type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }
}
