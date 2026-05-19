package com.integration.client.web;

import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 客户端渠道健康探测接口。
 */
@RestController
@RequestMapping("/api/app")
@Tag(name = "App client")
public class ClientPingController {

    /** 小程序 / 原生 App 等渠道的存活检测 */
    @GetMapping("/ping")
    @Operation(summary = "Mini-program and native app API health ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("channel", "app", "status", "UP"));
    }
}
