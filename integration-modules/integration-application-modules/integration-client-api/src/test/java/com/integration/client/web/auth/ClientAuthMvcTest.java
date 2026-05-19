package com.integration.client.web.auth;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientAuthMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void wxLoginThenAccessSecuredPing() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/app/auth/wx-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"test-wx-code\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        String token = JsonPath.read(login.getResponse().getContentAsString(), "$.data.accessToken");

        mockMvc.perform(get("/api/app/system/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.secured").value("true"));

        mockMvc.perform(get("/api/app/user/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.openid").value("mock-openid-test-wx-code"));

        mockMvc.perform(get("/api/app/system/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicPingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/app/ping"))
                .andExpect(status().isOk());
    }
}
