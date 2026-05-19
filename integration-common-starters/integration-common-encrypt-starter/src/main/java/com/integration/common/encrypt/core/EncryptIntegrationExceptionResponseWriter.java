package com.integration.common.encrypt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.exception.IntegrationException;
import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.common.encrypt.config.EncryptPathSupport;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.exception.EncryptException;
import com.integration.common.web.exception.IntegrationExceptionResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将 {@link IntegrationException} 写成 {@link ApiResult}，并按 {@link EncryptProperties} 决定是否加密为 key/data。
 * <p>
 * 供 Servlet Filter 等不走 {@link EncryptResponseBodyAdvice} 的入口使用，与正常接口响应加密规则一致。
 */
public final class EncryptIntegrationExceptionResponseWriter {

    private EncryptIntegrationExceptionResponseWriter() {
    }

    public static void write(HttpServletRequest request, HttpServletResponse response, ObjectMapper objectMapper,
                             EncryptProperties properties, PayloadAssistant payloadAssistant, IntegrationException ex)
            throws IOException {
        ApiResult<Void> body = IntegrationExceptionResponseWriter.toBody(ex);
        HttpStatus status = IntegrationExceptionResponseWriter.resolveStatus(ex);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String path = EncryptPathSupport.servletPath(request);
        Object payload = body;
        if (EncryptResponseSupport.shouldEncryptResponse(properties, path)) {
            payload = encryptOrFallback(body, properties, payloadAssistant);
        }
        objectMapper.writeValue(response.getOutputStream(), payload);
    }

    private static Object encryptOrFallback(ApiResult<Void> body, EncryptProperties properties,
                                          PayloadAssistant payloadAssistant) {
        if (!StringUtils.hasText(properties.getRsaPublicKey())) {
            return IntegrationExceptionResponseWriter.toBody(new EncryptException(
                    "已开启响应体加密（integration.encrypt.encrypt-response-body-enabled）但未配置 rsa-public-key"));
        }
        try {
            return payloadAssistant.encryptBody(body);
        } catch (Exception e) {
            return IntegrationExceptionResponseWriter.toBody(new EncryptException("响应加密失败", e));
        }
    }
}
