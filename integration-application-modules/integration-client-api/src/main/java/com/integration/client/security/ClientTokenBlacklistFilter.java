package com.integration.client.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientTokenBlacklistFilter extends OncePerRequestFilter {

    private final ClientTokenBlacklistService blacklistService;

    public ClientTokenBlacklistFilter(ClientTokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            String jti = jwtToken.getToken().getId();
            if (jti != null && blacklistService.isBlocked(jti)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("{\"success\":false,\"code\":401,\"message\":\"Unauthorized\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
