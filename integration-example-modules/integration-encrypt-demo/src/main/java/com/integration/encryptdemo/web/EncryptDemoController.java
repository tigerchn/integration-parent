package com.integration.encryptdemo.web;

import com.integration.encryptdemo.dto.CryptModel;
import com.integration.encryptdemo.dto.EchoRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/encrypt-demo")
public class EncryptDemoController {

    /**
     * 未加密接口，用于健康检查与联调对比（示例中通过 {@code exclude-paths} 排除响应加密）。
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "module", "integration-encrypt-demo");
    }

    /**
     * 全局请求解密开启时，除 {@code exclude-paths} 外对 JSON 请求尝试解密；解密后为 {@link EchoRequest}。
     * 全局响应加密开启时，本接口响应同样为 key/data 密文（与 {@code /secret} 一致），客户端需再解密。
     */
    @PostMapping("/echo")
    public CryptModel echo(@RequestBody CryptModel request) {
        System.out.println("name = " + request.getName());
        System.out.println("value = " + request.getValue());

        return request;
    }

    /**
     * 全局响应加密时，除排除项外返回体为 key/data 密文结构。
     */
    @GetMapping("/secret")
    public CryptModel secret() {
        return new CryptModel("demo-secret", LocalDate.now().toString());
    }

}
