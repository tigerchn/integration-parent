package com.integration.common.cache.inspect;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;

class CacheInspectUtilTest {

    @Test
    void inspectRegion_listsEntriesWithValues() {
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache cache = new CaffeineCache(
                "demo", Caffeine.newBuilder().recordStats().build());
        manager.setCaches(List.of(cache));
        manager.initializeCaches();

        cache.put("a", "1");
        cache.put("b", "2");

        CacheRegionSnapshot snapshot = CacheInspectUtil.inspectRegion(manager, "demo", 10)
                .orElseThrow();

        assertThat(snapshot.initialized()).isTrue();
        assertThat(snapshot.estimatedSize()).isEqualTo(2L);
        assertThat(snapshot.entries()).hasSize(2);
        assertThat(snapshot.entries())
                .anyMatch(e -> "a".equals(e.key()) && "1".equals(e.value()));
        assertThat(CacheInspectUtil.containsKey(manager, "demo", "a")).isTrue();
        assertThat(CacheInspectUtil.getEntry(manager, "demo", "a"))
                .map(CacheEntrySnapshot::value)
                .contains("1");
        assertThat(snapshot.stats()).isNotNull();
    }
}
