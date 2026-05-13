package com.integration.admin.web.open;

import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 无需认证的开放接口（如健康检查）。
 */
@RestController
@RequestMapping("/api/admin/open")
@Tag(name = "管理端-开放接口", description = "无需认证即可访问的公共接口（如健康检查）。")
public class AdminOpenController {

    /** 公开健康检查 */
    @GetMapping("/health")
    @Operation(
            summary = "健康检查",
            description = "返回渠道标识与运行状态，用于网关或运维探活。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "服务正常")
    })
    public ApiResult<Map<String, String>> health() {
        return ApiResult.ok(Map.of("channel", "admin", "status", "UP"));
    }
}
