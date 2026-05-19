package com.integration.admin.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

/**
 * 管理端安全相关错误文案（401/403），供过滤器与全局异常处理复用。
 */
public final class AdminSecurityErrorHints {

    private AdminSecurityErrorHints() {
    }

    /**
     * 未携带有效令牌或令牌无效时的提示。
     */
    public static String unauthorized(String requestUri, AuthenticationException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("未认证或访问令牌无效、已过期；请先调用 /api/admin/auth/login 获取 JWT，");
        sb.append("并在请求头 Authorization 中携带：Bearer {accessToken}。");
        if (StringUtils.hasText(requestUri)) {
            sb.append(" 请求路径：").append(requestUri).append("。");
        }
        if (ex != null && StringUtils.hasText(ex.getMessage())) {
            sb.append(" 详情：").append(ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * 已认证但权限不足（含 @PreAuthorize 拒绝）时的提示。
     */
    public static String forbidden(String requestUri, AccessDeniedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("权限不足：当前账号无权访问该接口，请确认已分配对应角色与接口权限（如 admin:system:ping 等）。");
        if (StringUtils.hasText(requestUri)) {
            sb.append(" 请求路径：").append(requestUri).append("。");
        }
        String detail = ex != null ? ex.getMessage() : null;
        if (StringUtils.hasText(detail) && !isGenericAccessDenied(detail)) {
            sb.append(" 说明：").append(detail);
        }
        return sb.toString();
    }

    private static boolean isGenericAccessDenied(String message) {
        String m = message.trim();
        return "Access Denied".equalsIgnoreCase(m) || "Access is denied".equalsIgnoreCase(m);
    }
}
