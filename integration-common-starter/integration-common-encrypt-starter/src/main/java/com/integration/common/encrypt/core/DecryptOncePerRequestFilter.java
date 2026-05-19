package com.integration.common.encrypt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.core.exception.IntegrationException;
import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.common.encrypt.config.EncryptPathSupport;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.exception.EncryptException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * {@link EncryptProperties#isDecryptRequestBodyEnabled()} 为 true 时，对 JSON 请求体按 key/data 解密；
 * 默认全路径生效，可通过 {@link EncryptProperties#getDecryptRequestPathPatterns()} 收窄，
 * {@link EncryptProperties#getExcludePaths()} 排除（如 {@code /actuator}）。
 */
public class DecryptOncePerRequestFilter extends OncePerRequestFilter implements Ordered {

    private final EncryptProperties properties;
    private final PayloadAssistant payloadAssistant;
    private final ObjectMapper objectMapper;

    public DecryptOncePerRequestFilter(EncryptProperties properties, PayloadAssistant payloadAssistant,
                                       ObjectMapper objectMapper) {
        this.properties = properties;
        this.payloadAssistant = payloadAssistant;
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!properties.isEnable() || !properties.isDecryptRequestBodyEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = EncryptPathSupport.servletPath(request);
        if (EncryptPathSupport.matchesExclude(path, properties.getExcludePaths())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!EncryptPathSupport.matchesIncludeOrAll(path, properties.getDecryptRequestPathPatterns())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!isJsonPayload(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!StringUtils.hasText(properties.getRsaPrivateKey())) {
                throw new EncryptException(
                        "已开启请求体解密（integration.encrypt.decrypt-request-body-enabled）但未配置 rsa-private-key");
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

            String realBody = payloadAssistant.decryptEnvelopeToPlainJson(body);
            DecryptRequestWrapper wrapper = new DecryptRequestWrapper(request, realBody);
            filterChain.doFilter(wrapper, response);
        } catch (IntegrationException ex) {
            EncryptIntegrationExceptionResponseWriter.write(
                    request, response, objectMapper, properties, payloadAssistant, ex);
        } catch (Exception e) {
            EncryptIntegrationExceptionResponseWriter.write(
                    request, response, objectMapper, properties, payloadAssistant,
                    new EncryptException("入参解密失败", e));
        }
    }

    private static boolean isJsonPayload(HttpServletRequest request) {
        String ct = request.getContentType();
        if (!StringUtils.hasText(ct)) {
            return false;
        }
        String lower = ct.toLowerCase(Locale.ROOT);
        return lower.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
