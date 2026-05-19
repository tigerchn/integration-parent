package com.integration.common.encrypt.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

/**
 * 与 {@link EncryptProperties} 中路径配置一致的匹配规则：含 {@code *} 或 {@code ?} 时用 Ant，否则按 URI 前缀匹配。
 */
public final class EncryptPathSupport {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private EncryptPathSupport() {
    }

    public static String servletPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (StringUtils.hasText(context) && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        return StringUtils.hasText(uri) ? uri : "/";
    }

    /**
     * 是否配置了至少一条非空路径规则（用于区分「全路径」与「仅匹配列表」）。
     */
    public static boolean hasRestrictivePatterns(String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return false;
        }
        for (String pattern : patterns) {
            if (StringUtils.hasText(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 无有效路径规则时视为匹配全部路径；否则仅当命中任一规则时为 true。
     */
    public static boolean matchesIncludeOrAll(String path, String[] includePatterns) {
        if (!hasRestrictivePatterns(includePatterns)) {
            return true;
        }
        return matchesAny(path, includePatterns);
    }

    /**
     * 命中任一排除规则则为 true（与 include 语法相同）。
     */
    public static boolean matchesExclude(String path, String[] excludePatterns) {
        return matchesAny(path, excludePatterns);
    }

    /**
     * 若 {@code patterns} 中无任何非空项则永不匹配。
     */
    public static boolean matchesAny(String path, String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return false;
        }
        for (String pattern : patterns) {
            if (!StringUtils.hasText(pattern)) {
                continue;
            }
            String p = pattern.trim();
            if (p.contains("*") || p.contains("?")) {
                if (MATCHER.match(p, path)) {
                    return true;
                }
            } else if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
