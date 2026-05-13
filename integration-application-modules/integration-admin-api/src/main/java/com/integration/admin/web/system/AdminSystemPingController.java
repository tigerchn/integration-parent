package com.integration.admin.web.system;

import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 需 JWT 与具体权限的管理端系统类接口。
 */
@RestController
@RequestMapping("/api/admin/system")
@Tag(name = "管理端-系统", description = "需携带有效 JWT，且通常要求具备相应方法级权限（如 admin:system:ping）。")
@SecurityRequirement(name = "bearer-jwt")
public class AdminSystemPingController {

    /**
     * 需要权限 {@code admin:system:ping} 的连通性探测。
     */
    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('admin:system:ping')")
    @Operation(
            summary = "系统连通性探测",
            description = "校验当前用户是否具备 `admin:system:ping` 权限；用于验证 JWT 与 RBAC 是否生效。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "已认证且具备权限"),
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "已认证但缺少 admin:system:ping 权限")
    })
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("channel", "admin", "scope", "system"));
    }
}
