package com.integration.client.web.user;

import com.integration.client.user.entity.ClientUser;
import com.integration.client.user.service.ClientUserService;
import com.integration.common.core.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/user")
@Tag(name = "小程序-用户", description = "需登录后访问的用户信息接口。")
@SecurityRequirement(name = "bearer-jwt")
public class ClientUserController {

    private final ClientUserService clientUserService;

    public ClientUserController(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('app:user:read')")
    @Operation(summary = "当前登录用户信息")
    public ApiResult<UserProfileView> me(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        Long userId = Long.parseLong(jwt.getSubject());
        ClientUser user = clientUserService.requireEnabledUser(userId);
        return ApiResult.ok(UserProfileView.from(user));
    }

    public record UserProfileView(Long id, String openid, String unionid, String nickname, String avatarUrl) {

        static UserProfileView from(ClientUser user) {
            return new UserProfileView(
                    user.getId(),
                    user.getOpenid(),
                    user.getUnionid(),
                    user.getNickname(),
                    user.getAvatarUrl());
        }
    }
}
