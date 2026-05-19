package com.integration.security.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationSecuritySampleApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void publicPingOkWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/public/ping")).andExpect(status().isOk());
    }

    @Test
    void securePingUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/secure/ping")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void securePingOkWithAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/secure/ping")).andExpect(status().isOk());
    }
}
