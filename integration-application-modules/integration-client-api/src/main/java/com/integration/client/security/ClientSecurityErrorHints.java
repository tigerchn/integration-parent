package com.integration.client.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

public final class ClientSecurityErrorHints {

    private ClientSecurityErrorHints() {
    }

    public static String unauthorized(String requestUri, AuthenticationException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("未认证或访问令牌无效、已过期；请先调用 /api/app/auth/wx-login 获取 JWT，");
        sb.append("并在请求头 Authorization 中携带：Bearer {accessToken}。");
        if (StringUtils.hasText(requestUri)) {
            sb.append(" 请求路径：").append(requestUri).append("。");
        }
        if (ex != null && StringUtils.hasText(ex.getMessage())) {
            sb.append(" 详情：").append(ex.getMessage());
        }
        return sb.toString();
    }

    public static String forbidden(String requestUri, AccessDeniedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("权限不足：当前用户无权访问该接口。");
        if (StringUtils.hasText(requestUri)) {
            sb.append(" 请求路径：").append(requestUri).append("。");
        }
        String detail = ex != null ? ex.getMessage() : null;
        if (StringUtils.hasText(detail) && !"Access Denied".equalsIgnoreCase(detail.trim())) {
            sb.append(" 说明：").append(detail);
        }
        return sb.toString();
    }
}
