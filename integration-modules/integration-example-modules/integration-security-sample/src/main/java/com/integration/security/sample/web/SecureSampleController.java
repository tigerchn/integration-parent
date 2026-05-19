package com.integration.security.sample.web;

import com.integration.common.core.api.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 需要登录后访问的示例接口。
 */
@RestController
@RequestMapping("/api/secure")
public class SecureSampleController {

    /** 受保护范围连通检测 */
    @GetMapping("/ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("scope", "secure"));
    }
}
