package com.integration.common.encrypt.core;

import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.common.encrypt.config.EncryptPathSupport;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.exception.EncryptException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@link EncryptProperties#isEncryptResponseBodyEnabled()} 为 true 时加密响应体；
 * 默认全路径生效，可通过 {@link EncryptProperties#getEncryptResponsePathPatterns()} 收窄，
 * {@link EncryptProperties#getExcludePaths()} 排除。
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final EncryptProperties properties;
    private final PayloadAssistant payloadAssistant;

    public EncryptResponseBodyAdvice(EncryptProperties properties, PayloadAssistant payloadAssistant) {
        this.properties = properties;
        this.payloadAssistant = payloadAssistant;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return properties.isEnable() && properties.isEncryptResponseBodyEnabled();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        String path = requestPath(request);
        if (!EncryptResponseSupport.shouldEncryptResponse(properties, path)) {
            return body;
        }

        if (!StringUtils.hasText(properties.getRsaPublicKey())) {
            throw new EncryptException("已开启响应体加密（integration.encrypt.encrypt-response-body-enabled）但未配置 rsa-public-key");
        }

        try {
            return payloadAssistant.encryptBody(body);
        } catch (EncryptException e) {
            throw e;
        } catch (Exception e) {
            throw new EncryptException("响应加密失败", e);
        }
    }

    private static String requestPath(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            return EncryptPathSupport.servletPath(req);
        }
        String raw = request.getURI().getPath();
        return StringUtils.hasText(raw) ? raw : "/";
    }
}
