package com.integration.encryptdemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.encryptdemo.dto.DecryptModel;
import com.integration.encryptdemo.dto.EncryptModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "Encrypt demo", description = "请求解密 / 响应加密演示（除 exclude-paths 外为密文）")
@RestController
@RequestMapping("/api/encrypt-demo")
public class EncryptDemoController {

    @Resource
    PayloadAssistant payloadAssistant;

    /**
     * 未加密接口，用于健康检查与联调对比（示例中通过 {@code exclude-paths} 排除响应加密）。
     */
    @Operation(summary = "Health check (plaintext, excluded from encrypt)")
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "module", "integration-encrypt-demo");
    }

    /**
     * 全局请求解密开启时，除 {@code exclude-paths} 外对 JSON 请求尝试解密；解密后为 {@link EncryptModel}。
     * 全局响应加密开启时，本接口响应同样为 key/data 密文（与 {@code /secret} 一致），客户端需再解密。
     */
    @Operation(summary = "Echo with request decrypt and encrypted response")
    @PostMapping("/echo")
    public ApiResult<EncryptModel> echo(@RequestBody EncryptModel request) {
        return ApiResult.ok(request);
    }

    /**
     * 全局响应加密时，除排除项外返回体为 key/data 密文结构。
     */
    @Operation(summary = "Sample secret payload (encrypted response)")
    @GetMapping("/secret")
    public ApiResult<EncryptModel> secret() {
        EncryptModel encryptModel = new EncryptModel("demo-secret", LocalDate.now().toString());
        return ApiResult.ok(encryptModel);
    }

    @Operation(summary = "encrypt")
    @PostMapping("/encrypt")
    public ApiResult<String> encrypt(@RequestBody EncryptModel request){
        String encrypt = payloadAssistant.encrypt(request);
        return ApiResult.ok(encrypt);
    }

    @Operation(summary = "decrypt")
    @PostMapping("/decrypt")
    public ApiResult<EncryptModel> decrypt(@RequestBody DecryptModel request){
        EncryptModel decrypt = payloadAssistant.decrypt(request.getValue(), EncryptModel.class);
        return ApiResult.ok(decrypt);
    }
}
