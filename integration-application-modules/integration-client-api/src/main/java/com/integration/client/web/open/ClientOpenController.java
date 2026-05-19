package com.integration.client.web.open;

import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/app/open")
@Tag(name = "小程序-开放", description = "无需登录的公开接口。")
public class ClientOpenController {

    @GetMapping("/info")
    @Operation(summary = "开放信息")
    public ApiResult<Map<String, String>> info() {
        return ApiResult.ok(Map.of("module", "integration-client-api", "auth", "wx-login"));
    }
}
