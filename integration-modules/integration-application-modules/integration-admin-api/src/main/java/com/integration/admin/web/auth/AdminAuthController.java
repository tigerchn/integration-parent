package com.integration.admin.web.auth;

import com.integration.admin.security.AdminTokenBlacklistService;
import com.integration.admin.security.AdminTokenService;
import com.integration.admin.security.AdminUserDetails;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

/**
 * 管理端认证接口：登录签发 JWT、登出加入黑名单等。
 */
@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "管理端-认证", description = "用户名密码登录获取 JWT；携带 Bearer Token 登出并可写入黑名单（需启用 Redis 黑名单）。")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final AdminTokenService tokenService;
    private final AdminTokenBlacklistService tokenBlacklistService;

    /**
     * @param authenticationManager 用户名密码认证管理器
     * @param tokenService            JWT 签发服务
     * @param tokenBlacklistService   令牌黑名单（登出）
     */
    public AdminAuthController(AuthenticationManager authenticationManager,
                               AdminTokenService tokenService,
                               AdminTokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * 用户名密码登录，成功返回访问令牌及过期时间。
     */
    @PostMapping("/login")
    @Operation(
            summary = "登录获取访问令牌",
            description = "使用用户名与密码认证，成功后返回 JWT（HS256）及过期时间（秒）。凭证错误时返回 401。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "认证成功，返回 accessToken 与过期秒数"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            AdminUserDetails principal = (AdminUserDetails) authentication.getPrincipal();
            AdminTokenService.TokenIssueResult token = tokenService.issueAccessToken(
                    principal.getUserId(), principal.getUsername());
            LoginResponse body = new LoginResponse(token.accessToken(), "Bearer", token.expiresInSeconds());
            return ResponseEntity.ok(ApiResult.ok(body));
        } catch (BadCredentialsException ex) {
            ApiResult<LoginResponse> body = ApiResult.fail(ResultCode.UNAUTHORIZED, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }

    /**
     * 将当前 JWT 的 jti 写入 Redis 黑名单（若已启用），直至令牌自然过期。
     */
    @PostMapping("/logout")
    @Operation(
            summary = "登出（撤销当前令牌）",
            description = "解析当前 Bearer JWT 的 jti，在启用 Redis 黑名单时将 jti 写入 Redis，直至令牌自然过期；未启用黑名单时仍返回成功。"
    )
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登出成功（无响应体数据）")
    })
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

    @Schema(name = "AdminLoginRequest", description = "管理端登录请求")
    public record LoginRequest(
            @Schema(description = "登录用户名", example = "admin") @NotBlank String username,
            @Schema(description = "登录密码", example = "change-me") @NotBlank String password) {
    }

    @Schema(name = "AdminLoginResponse", description = "管理端登录成功后的令牌信息")
    public record LoginResponse(
            @Schema(description = "JWT 访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...") String accessToken,
            @Schema(description = "令牌类型", example = "Bearer") String tokenType,
            @Schema(description = "过期时间（秒）", example = "28800") long expiresInSeconds) {
    }
}
