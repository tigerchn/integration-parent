package com.integration.encryptdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "integration.encrypt.encrypt-response-body-enabled=true")
@AutoConfigureMockMvc
class EncryptDemoEncryptedErrorResponseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void echoWithPlainJsonReturnsEncryptedErrorEnvelope() throws Exception {
        mockMvc.perform(post("/api/encrypt-demo/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"demo\",\"value\":\"plain\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.success").doesNotExist());
    }
}
