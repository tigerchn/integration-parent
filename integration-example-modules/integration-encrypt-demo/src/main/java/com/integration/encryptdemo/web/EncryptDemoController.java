package com.integration.encryptdemo.web;

import com.integration.common.encrypt.annotation.ApiDecrypt;
import com.integration.common.encrypt.annotation.ApiEncrypt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/encrypt-demo")
public class EncryptDemoController {

    /**
     * 未加密接口，用于健康检查与联调对比。
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "module", "integration-encrypt-demo");
    }

    /**
     * 请求体需为密文 JSON：{@code {"key":"...","data":"..."}}，过滤器解密后反序列化为 {@link SecretPayload}。
     */
    @PostMapping("/echo")
    @ApiDecrypt
    public Map<String, Object> echo(@RequestBody SecretPayload request) {
        return Map.of(
                "echo", request.label(),
                "receivedAt", request.generatedAt);
    }

    /**
     * 响应体经 {@link ApiEncrypt} 包装为 key、data 密文字段。
     */
    @GetMapping("/secret")
    @ApiEncrypt
    public SecretPayload secret() {
        return new SecretPayload("demo-secret", Instant.now().toString());
    }

    public record SecretPayload(String label, String generatedAt) {
    }
}
