package com.integration.common.cache.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.integration.common.cache.inspect.CacheEntrySnapshot;
import com.integration.common.cache.inspect.LocatedCacheEntry;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;

class LocalCacheUtilTest {

    @Test
    void hasKeyAndGetEntry_withinNamedRegion() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();
        cache.put("user-1", "payload");

        LocalCacheUtil util = new LocalCacheUtil(manager);

        assertThat(util.hasKey("demoEcho", "user-1")).isTrue();
        assertThat(util.hasKey("demoEcho", "missing")).isFalse();
        assertThat(util.hasKey("other", "user-1")).isFalse();

        CacheEntrySnapshot snapshot = util.getEntry("demoEcho", "user-1").orElseThrow();
        assertThat(snapshot.key()).isEqualTo("user-1");
        assertThat(snapshot.value()).isEqualTo("payload");

        assertThat(util.get("demoEcho", "user-1", String.class)).contains("payload");
    }

    @Test
    void findEntry_scansAllRegions() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();
        cache.put("k", 42);

        LocalCacheUtil util = new LocalCacheUtil(manager);

        assertThat(util.hasKey("k")).isTrue();
        LocatedCacheEntry located = util.findEntry("k").orElseThrow();
        assertThat(located.cacheName()).isEqualTo("demoEcho");
        assertThat(located.entry().value()).isEqualTo("42");
    }

    @Test
    void totalCount_sumsEntriesAcrossRegions() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache dev = new CaffeineCache("cache_dev", Caffeine.newBuilder().build());
        CaffeineCache test = new CaffeineCache("cache_test", Caffeine.newBuilder().build());
        manager.setCaches(List.of(dev, test));
        manager.initializeCaches();
        dev.put("a", "1");
        dev.put("b", "2");
        test.put("x", "9");

        LocalCacheUtil util = new LocalCacheUtil(manager);

        assertThat(util.count("cache_dev")).isEqualTo(2L);
        assertThat(util.count("cache_test")).isEqualTo(1L);
        assertThat(util.totalCount()).isEqualTo(3L);
        assertThat(util.count("not-exists")).isZero();
    }

    @Test
    void evict_removesSingleKey() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();
        cache.put("user-1", "payload");
        cache.put("user-2", "other");

        LocalCacheUtil util = new LocalCacheUtil(manager);

        assertThat(util.evict("demoEcho", "user-1")).isTrue();
        assertThat(util.hasKey("demoEcho", "user-1")).isFalse();
        assertThat(util.hasKey("demoEcho", "user-2")).isTrue();
        assertThat(util.evict("demoEcho", "missing")).isFalse();
        assertThat(util.evict("not-exists", "user-2")).isFalse();
    }

    @Test
    void clear_removesAllEntriesInRegion() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();
        cache.put("a", "1");
        cache.put("b", "2");

        LocalCacheUtil util = new LocalCacheUtil(manager);

        util.clear("demoEcho");

        assertThat(util.totalCount()).isZero();
        util.clear("not-exists");
    }

    @Test
    void evictAndClearAll_scanAllRegions() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache dev = new CaffeineCache("cache_dev", Caffeine.newBuilder().build());
        CaffeineCache test = new CaffeineCache("cache_test", Caffeine.newBuilder().build());
        manager.setCaches(List.of(dev, test));
        manager.initializeCaches();
        dev.put("shared", "dev");
        test.put("shared", "test");
        test.put("only-test", "x");

        LocalCacheUtil util = new LocalCacheUtil(manager);

        assertThat(util.evict("shared")).isTrue();
        assertThat(util.hasKey("shared")).isFalse();
        assertThat(util.hasKey("cache_test", "only-test")).isTrue();

        util.clearAll();
        assertThat(util.totalCount()).isZero();
    }

    @Test
    void put_writesEntry() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();

        LocalCacheUtil util = new LocalCacheUtil(manager);
        util.put("demoEcho", "k", "v");

        assertThat(util.get("demoEcho", "k", String.class)).contains("v");
    }

    @Test
    void getOrLoad_runsLoaderOnlyOnMiss() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache("demoEcho", Caffeine.newBuilder().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();

        LocalCacheUtil util = new LocalCacheUtil(manager);
        AtomicInteger loads = new AtomicInteger();

        String first = util.getOrLoad("demoEcho", "user-1", String.class, () -> {
            loads.incrementAndGet();
            return "computed";
        });
        String second = util.getOrLoad("demoEcho", "user-1", String.class, () -> {
            loads.incrementAndGet();
            return "ignored";
        });

        assertThat(first).isEqualTo("computed");
        assertThat(second).isEqualTo("computed");
        assertThat(loads).hasValue(1);
    }

    @Test
    void getOrLoad_withoutCacheRegion_stillReturnsLoaderResult() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of());
        manager.initializeCaches();

        LocalCacheUtil util = new LocalCacheUtil(manager);
        AtomicInteger loads = new AtomicInteger();

        assertThat(util.getOrLoad("missing", "k", () -> {
            loads.incrementAndGet();
            return 7;
        })).isEqualTo(7);
        assertThat(loads).hasValue(1);
        assertThat(util.hasKey("missing", "k")).isFalse();
    }
}
