package com.integration.admin;

import com.integration.admin.web.auth.AdminAuthController;
import com.integration.common.encrypt.assistant.PayloadAssistant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class IntegrationAdminApiApplicationTests {

    @Resource
    private PayloadAssistant payloadAssistant;

    @Test
    void contextLoads() {
        AdminAuthController.LoginRequest request = new AdminAuthController.LoginRequest("admin", "admin123");
        String encrypt = payloadAssistant.encrypt(request);
        System.out.println("encrypt = " + encrypt);
    }
}
