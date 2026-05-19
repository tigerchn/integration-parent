package com.integration.client.web.system;

import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/app/system")
@Tag(name = "小程序-系统", description = "需登录的受保护接口示例。")
@SecurityRequirement(name = "bearer-jwt")
public class ClientSystemController {

    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('app:user:read')")
    @Operation(summary = "受保护的存活检测（需 JWT）")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("channel", "app", "status", "UP", "secured", "true"));
    }
}
