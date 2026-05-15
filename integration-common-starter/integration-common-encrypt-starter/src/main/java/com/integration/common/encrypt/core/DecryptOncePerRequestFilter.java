package com.integration.common.encrypt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.annotation.ApiDecrypt;
import com.integration.common.encrypt.dto.EncryptedRequestBody;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.exception.EncryptException;
import com.integration.common.encrypt.util.AesGcmUtil;
import com.integration.common.encrypt.util.RsaUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 仅当目标处理器带有 {@link ApiDecrypt} 且开启全局配置时，对 JSON 请求体按约定解密并包装请求。
 */
public class DecryptOncePerRequestFilter extends OncePerRequestFilter implements Ordered {

    private final EncryptProperties properties;
    private final ObjectMapper objectMapper;
    private final List<HandlerMapping> handlerMappings;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DecryptOncePerRequestFilter(EncryptProperties properties, ObjectMapper objectMapper,
                                       List<HandlerMapping> handlerMappings) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.handlerMappings = handlerMappings;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!properties.isEnable() || isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HandlerMethod handlerMethod = resolveHandlerMethod(request);
        if (handlerMethod == null || !requiresDecrypt(handlerMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!isJsonPayload(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(properties.getRsaPrivateKey())) {
            throw new EncryptException("已声明 @ApiDecrypt 但未配置 integration.encrypt.rsa-private-key");
        }

        byte[] raw = request.getInputStream().readAllBytes();
        if (raw.length == 0) {
            filterChain.doFilter(request, response);
            return;
        }

        String body = new String(raw, StandardCharsets.UTF_8);
        if (!StringUtils.hasText(body)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            EncryptedRequestBody encrypted = objectMapper.readValue(body, EncryptedRequestBody.class);
            if (encrypted == null || !StringUtils.hasText(encrypted.key()) || !StringUtils.hasText(encrypted.data())) {
                throw new EncryptException("入参解密失败：请求体须为 JSON 对象且包含非空的 key、data 字段");
            }

            String aesKey = RsaUtil.decrypt(encrypted.key(), properties.getRsaPrivateKey(), properties.getRsaTransformation());
            String realBody = AesGcmUtil.decrypt(encrypted.data(), aesKey);

            DecryptRequestWrapper wrapper = new DecryptRequestWrapper(request, realBody);
            filterChain.doFilter(wrapper, response);
        } catch (EncryptException e) {
            throw e;
        } catch (Exception e) {
            throw new EncryptException("入参解密失败", e);
        }
    }

    private boolean isExcluded(HttpServletRequest request) {
        String path = requestPath(request);
        for (String pattern : properties.getExcludePaths()) {
            if (!StringUtils.hasText(pattern)) {
                continue;
            }
            String p = pattern.trim();
            if (p.contains("*") || p.contains("?")) {
                if (pathMatcher.match(p, path)) {
                    return true;
                }
            } else if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    private static String requestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (StringUtils.hasText(context) && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        return StringUtils.hasText(uri) ? uri : "/";
    }

    private static boolean isJsonPayload(HttpServletRequest request) {
        String ct = request.getContentType();
        if (!StringUtils.hasText(ct)) {
            return false;
        }
        String lower = ct.toLowerCase(Locale.ROOT);
        return lower.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private HandlerMethod resolveHandlerMethod(HttpServletRequest request) {
        for (HandlerMapping hm : handlerMappings) {
            try {
                HandlerExecutionChain chain = hm.getHandler(request);
                if (chain != null && chain.getHandler() instanceof HandlerMethod hmHandler) {
                    return hmHandler;
                }
            } catch (Exception ignored) {
                // 与其它 HandlerMapping 尝试一致：忽略当前 mapping 的解析异常
            }
        }
        return null;
    }

    private static boolean requiresDecrypt(HandlerMethod handlerMethod) {
        ApiDecrypt onMethod = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), ApiDecrypt.class);
        if (onMethod != null) {
            return onMethod.value();
        }
        ApiDecrypt onType = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), ApiDecrypt.class);
        return onType != null && onType.value();
    }
}
