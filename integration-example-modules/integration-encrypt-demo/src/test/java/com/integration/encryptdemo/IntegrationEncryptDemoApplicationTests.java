package com.integration.encryptdemo;

import com.integration.encryptdemo.dto.EchoRequest;
import com.integration.encryptdemo.util.EncryptEchoRequestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationEncryptDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoadsAndPingIsOpen() throws Exception {
        mockMvc.perform(get("/api/encrypt-demo/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void echoWithUtilGeneratedBody() throws Exception {
        String body = EncryptEchoRequestUtil.toEncryptedJson(new EchoRequest("hello-from-util"));
        System.out.println("body = " + body);
        mockMvc.perform(post("/api/encrypt-demo/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.echo").value("hello-from-util"));
    }
}
