package com.integration.cachedemo.web;

import com.integration.cachedemo.dto.CacheEchoPayload;
import com.integration.cachedemo.dto.CacheKeyDetail;
import com.integration.cachedemo.dto.CacheKeyPresence;
import com.integration.cachedemo.service.CacheDemoService;
import com.integration.common.cache.inspect.CacheEntrySnapshot;
import com.integration.common.cache.inspect.CacheInspectUtil;
import com.integration.common.cache.inspect.CacheRegionSnapshot;
import com.integration.common.cache.inspect.LocatedCacheEntry;
import com.integration.common.cache.util.LocalCacheUtil;
import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本地缓存演示：连续相同 key 的 {@link #echo} 应秒回且 body 与首次一致；{@link #evict} 后再次请求会重新“计算”。
 */
@Tag(name = "Cache demo")
@RestController
@RequestMapping("/api/cache-demo")
public class CacheDemoController {

    private final CacheDemoService cacheDemoService;
    private final LocalCacheUtil localCacheUtil;
    private final CacheManager cacheManager;

    public CacheDemoController(
            CacheDemoService cacheDemoService, LocalCacheUtil localCacheUtil, CacheManager cacheManager) {
        this.cacheDemoService = cacheDemoService;
        this.localCacheUtil = localCacheUtil;
        this.cacheManager = cacheManager;
    }

    /**
     * 按 key 读取或写入缓存；首次调用约 {@link CacheEchoPayload#simulatedCostMs()} 毫秒，命中缓存时几乎无等待。
     */
    @Operation(summary = "Echo with @Cacheable (first call slow, same key instant)")
    @GetMapping("/echo")
    public ApiResult<CacheEchoPayload> echo(@RequestParam("key") String key) {
        return ApiResult.ok(cacheDemoService.echo(requireKey(key)));
    }

    /** 逐 key 失效 */
    @Operation(summary = "Evict one cache key")
    @GetMapping("/evict")
    public ApiResult<String> evict(@RequestParam("key") String key) {
        String trimmed = requireKey(key);
        cacheDemoService.evict(trimmed);
        return ApiResult.ok("evicted: " + trimmed);
    }

    /** 清空本演示使用的缓存名 {@code demoEcho} 下全部条目 */
    @Operation(summary = "Clear entire demoEcho cache")
    @GetMapping("/clear")
    public ApiResult<String> clear() {
        cacheDemoService.clearAll();
        return ApiResult.ok("cleared cache demoEcho");
    }

    @Operation(summary = "Check whether key exists in demoEcho local cache")
    @GetMapping("/inspect/has-key")
    public ApiResult<CacheKeyPresence> inspectHasKey(@RequestParam("key") String key) {
        String trimmed = requireKey(key);
        boolean cached = localCacheUtil.hasKey(CacheDemoService.CACHE_NAME, trimmed);
        return ApiResult.ok(new CacheKeyPresence(CacheDemoService.CACHE_NAME, trimmed, cached));
    }

    @Operation(summary = "Get cache entry snapshot and typed value for key in demoEcho")
    @GetMapping("/inspect/entry")
    public ApiResult<CacheKeyDetail> inspectEntry(@RequestParam("key") String key) {
        String trimmed = requireKey(key);
        String cacheName = CacheDemoService.CACHE_NAME;
        boolean cached = localCacheUtil.hasKey(cacheName, trimmed);
        CacheEntrySnapshot snapshot =
                localCacheUtil.getEntry(cacheName, trimmed).orElse(null);
        CacheEchoPayload value =
                localCacheUtil.get(cacheName, trimmed, CacheEchoPayload.class).orElse(null);
        return ApiResult.ok(new CacheKeyDetail(cacheName, trimmed, cached, snapshot, value));
    }

    @Operation(summary = "Find key across all registered local cache regions")
    @GetMapping("/inspect/find")
    public ApiResult<LocatedCacheEntry> inspectFind(@RequestParam("key") String key) {
        Optional<LocatedCacheEntry> located = localCacheUtil.findEntry(requireKey(key));
        return located.map(ApiResult::ok).orElseGet(() -> ApiResult.ok(null));
    }

    @Operation(summary = "List all local cache regions with sampled entries")
    @GetMapping("/inspect/regions")
    public ApiResult<List<CacheRegionSnapshot>> inspectRegions(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        int sampleLimit = Math.max(0, Math.min(limit, 500));
        return ApiResult.ok(CacheInspectUtil.inspectAll(cacheManager, sampleLimit));
    }

    private static String requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        return key.trim();
    }
}
