package com.integration.security.sample.web;

import com.integration.common.core.api.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 无需认证的示例接口。
 */
@RestController
@RequestMapping("/api/public")
public class PublicSampleController {

    /** 公开范围连通检测 */
    @GetMapping("/ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("scope", "public"));
    }
}
