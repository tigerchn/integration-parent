package com.integration.admin.web.auth;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理端认证与受保护接口的 MVC 联调。
 * <p>若仅在 IntelliJ 中运行仍出现 ByteBuddy / CDS 红色 JVM 告警，可在该运行配置的 VM options 中加入：
 * {@code -XX:+EnableDynamicAgentLoading -Xshare:off}（命令行 {@code mvn test} 已由根 {@code pom.xml} 的 Surefire 默认注入）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuthMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginThenAccessSecuredPing() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = login.getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.data.accessToken");
        System.out.println("token = " + token);

        mockMvc.perform(get("/api/admin/system/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/system/ping"))
                .andExpect(status().isUnauthorized());


    }

    @Test
    void loginFailsWithBadPassword() throws Exception {
        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }
}
