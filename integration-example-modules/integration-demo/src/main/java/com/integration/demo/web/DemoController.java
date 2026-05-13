package com.integration.demo.web;

import com.integration.common.tool.json.Jsons;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 演示用 REST：校验、业务异常、JSON 工具等示例。
 */
@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo")
public class DemoController {

    private final Jsons jsons;

    /**
     * @param jsons JSON 序列化工具
     */
    public DemoController(Jsons jsons) {
        this.jsons = jsons;
    }

    /** 简单存活检测 */
    @GetMapping("/ping")
    @Operation(summary = "Health-style ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("status", "UP"));
    }

    /** 回显路径变量 */
    @GetMapping("/echo/{text}")
    @Operation(summary = "Echo path variable")
    public ApiResult<Map<String, String>> echo(@PathVariable String text) {
        return ApiResult.ok(Map.of("echo", text));
    }

    /** 请求体验证示例 */
    @PostMapping("/validate")
    @Operation(summary = "Validation sample")
    public ApiResult<Void> validate(@RequestBody DemoRequest request) {
        return ApiResult.ok();
    }

    /** 主动抛出 {@link BizException} 以观察全局处理 */
    @GetMapping("/biz-error")
    @Operation(summary = "Throws business exception")
    public ApiResult<Void> bizError() {
        throw new BizException(ResultCode.BUSINESS_ERROR, "demo business failure");
    }

    /** 使用 {@link Jsons} 做一次序列化与反序列化往返 */
    @GetMapping("/json-roundtrip")
    @Operation(summary = "Uses Jsons helper from tool starter")
    public ApiResult<Map<String, Object>> jsonRoundtrip() {
        String json = jsons.write(Map.of("k", "v"));
        Map<?, ?> parsed = jsons.read(json, Map.class);
        return ApiResult.ok(Map.of("original", json, "parsed", parsed));
    }

    /** 演示请求体 */
    public record DemoRequest(@NotBlank String name) {
    }
}
