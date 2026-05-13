package com.integration.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 在 Bearer JWT 认证通过后检查 jti 是否已被黑名单拒绝。
 */
public class AdminTokenBlacklistFilter extends OncePerRequestFilter {

    private final AdminTokenBlacklistService blacklistService;

    /**
     * @param blacklistService 黑名单查询服务
     */
    public AdminTokenBlacklistFilter(AdminTokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    /**
     * 若当前认证为 JWT 且 jti 在黑名单中，则直接返回 401 JSON。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            String jti = jwtToken.getToken().getId();
            if (jti != null && blacklistService.isBlocked(jti)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\",\"data\":null}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
