package com.integration.cachedemo.web;

import com.integration.cachedemo.dto.CacheEchoPayload;
import com.integration.cachedemo.service.CacheDemoService;
import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public CacheDemoController(CacheDemoService cacheDemoService) {
        this.cacheDemoService = cacheDemoService;
    }

    /**
     * 按 key 读取或写入缓存；首次调用约 {@link CacheEchoPayload#simulatedCostMs()} 毫秒，命中缓存时几乎无等待。
     */
    @Operation(summary = "Echo with @Cacheable (first call slow, same key instant)")
    @GetMapping("/echo")
    public ApiResult<CacheEchoPayload> echo(@RequestParam("key") String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        return ApiResult.ok(cacheDemoService.echo(key.trim()));
    }

    /** 逐 key 失效 */
    @Operation(summary = "Evict one cache key")
    @GetMapping("/evict")
    public ApiResult<String> evict(@RequestParam("key") String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        cacheDemoService.evict(key.trim());
        return ApiResult.ok("evicted: " + key.trim());
    }

    /** 清空本演示使用的缓存名 {@code demoEcho} 下全部条目 */
    @Operation(summary = "Clear entire demoEcho cache")
    @GetMapping("/clear")
    public ApiResult<String> clear() {
        cacheDemoService.clearAll();
        return ApiResult.ok("cleared cache demoEcho");
    }
}
