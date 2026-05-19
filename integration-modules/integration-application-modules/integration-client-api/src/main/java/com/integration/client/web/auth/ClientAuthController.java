package com.integration.client.web.auth;

import com.integration.client.auth.ClientAuthService;
import com.integration.client.security.ClientTokenBlacklistService;
import com.integration.client.security.ClientTokenService;
import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/app/auth")
@Tag(name = "小程序-认证", description = "微信小程序 wx.login() code 换 JWT；Bearer 登出撤销令牌。")
public class ClientAuthController {

    private final ClientAuthService clientAuthService;
    private final ClientTokenBlacklistService tokenBlacklistService;

    public ClientAuthController(ClientAuthService clientAuthService,
                                ClientTokenBlacklistService tokenBlacklistService) {
        this.clientAuthService = clientAuthService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/wx-login")
    @Operation(
            summary = "微信小程序登录",
            description = "使用 wx.login() 返回的 code 调用微信 jscode2session，自动注册/登录并签发 JWT。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "微信 code 无效或已过期")
    })
    public ApiResult<WxLoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        ClientTokenService.TokenIssueResult token = clientAuthService.wxLogin(request.code());
        return ApiResult.ok(new WxLoginResponse(token.accessToken(), "Bearer", token.expiresInSeconds()));
    }

    @PostMapping("/logout")
    @Operation(summary = "登出（撤销当前令牌）")
    @SecurityRequirement(name = "bearer-jwt")
    public ApiResult<Void> logout(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String jti = jwt.getId();
            Instant exp = jwt.getExpiresAt() != null ? jwt.getExpiresAt() : Instant.now();
            Duration ttl = Duration.between(Instant.now(), exp);
            if (jti != null && !ttl.isNegative() && !ttl.isZero()) {
                tokenBlacklistService.add(jti, ttl);
            }
        }
        return ApiResult.ok();
    }

    @Schema(name = "WxLoginRequest")
    public record WxLoginRequest(
            @Schema(description = "wx.login() 返回的临时登录凭证", example = "081abcXYZ") @NotBlank String code) {
    }

    @Schema(name = "WxLoginResponse")
    public record WxLoginResponse(
            @Schema(description = "JWT 访问令牌") String accessToken,
            @Schema(description = "令牌类型", example = "Bearer") String tokenType,
            @Schema(description = "过期时间（秒）", example = "604800") long expiresInSeconds) {
    }
}
