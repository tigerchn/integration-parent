package com.integration.common.encrypt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.annotation.ApiEncrypt;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.exception.EncryptException;
import com.integration.common.encrypt.util.AesGcmUtil;
import com.integration.common.encrypt.util.RsaUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final EncryptProperties properties;
    private final ObjectMapper objectMapper;

    public EncryptResponseBodyAdvice(EncryptProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return properties.isEnable() && requiresEncryptResponse(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!StringUtils.hasText(properties.getRsaPublicKey())) {
            throw new EncryptException("已声明 @ApiEncrypt 但未配置 integration.encrypt.rsa-public-key");
        }

        try {
            String aesKey = AesGcmUtil.generateKey(properties.getAesKeySize());
            String plainJson = body instanceof String s ? s : objectMapper.writeValueAsString(body);
            String data = AesGcmUtil.encrypt(plainJson, aesKey);
            String key = RsaUtil.encrypt(aesKey, properties.getRsaPublicKey(), properties.getRsaTransformation());

            Map<String, String> res = new HashMap<>();
            res.put("key", key);
            res.put("data", data);
            return res;
        } catch (EncryptException e) {
            throw e;
        } catch (Exception e) {
            throw new EncryptException("响应加密失败", e);
        }
    }

    private static boolean requiresEncryptResponse(MethodParameter returnType) {
        Method method = returnType.getMethod();
        if (method != null) {
            ApiEncrypt onMethod = AnnotatedElementUtils.findMergedAnnotation(method, ApiEncrypt.class);
            if (onMethod != null) {
                return onMethod.value();
            }
        }
        ApiEncrypt onType = AnnotatedElementUtils.findMergedAnnotation(returnType.getContainingClass(), ApiEncrypt.class);
        return onType != null && onType.value();
    }
}
