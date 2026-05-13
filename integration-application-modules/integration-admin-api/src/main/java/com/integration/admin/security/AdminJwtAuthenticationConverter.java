package com.integration.admin.security;

import com.integration.admin.rbac.service.AdminRbacQueryService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * 将已校验的 {@link Jwt} 转为带权限的 {@link JwtAuthenticationToken}。
 */
@Component
public class AdminJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AdminRbacQueryService rbacQueryService;

    /**
     * @param rbacQueryService 用于按用户 ID 加载权限
     */
    public AdminJwtAuthenticationConverter(AdminRbacQueryService rbacQueryService) {
        this.rbacQueryService = rbacQueryService;
    }

    /**
     * {@inheritDoc}
     *
     * @param jwt 已解码的访问令牌
     * @return 带权限的 JWT 认证令牌
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return new JwtAuthenticationToken(jwt, rbacQueryService.loadPermissionAuthorities(userId));
    }
}
