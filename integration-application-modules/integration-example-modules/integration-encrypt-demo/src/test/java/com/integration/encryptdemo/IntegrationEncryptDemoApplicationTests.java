package com.integration.encryptdemo;

import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.common.encrypt.assistant.RsaKeyPairAssistant;
import com.integration.encryptdemo.dto.EncryptModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

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
    void echoWithPlainJsonReturnsApiResultBadRequest() throws Exception {
        mockMvc.perform(post("/api/encrypt-demo/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"demo\",\"value\":\"plain\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.status").doesNotExist());
    }


    @Autowired
    PayloadAssistant payloadAssistant;

    @Test
    void encryptDecrypt() {
        /*CryptModel cryptModel = new CryptModel("刘小敏", "12345678");
        String encrypt = payloadAssistant.encrypt(cryptModel);
        System.out.println("encrypt = " + encrypt);



        CryptModel decrypt = payloadAssistant.decrypt(encrypt, CryptModel.class);
        System.out.println("name = " + decrypt.getName());
        System.out.println("value = " + decrypt.getValue());
        */

        String text = "{\n" +
                "    \"key\": \"WXxYnvoWwbmFGJ9CfaaClDYJ2fnGgFiP8oa24DbjYTgvF79674FQ/XiKQF7ROJNNZ62I78d6wHhApF3+Y0gDBm6jnFTINTzqCpVlG/2B/iGnpA5GC6XG9IHJ5wPzsFVQUtFuQph7GXdFd25pn4iyRW6QxH0XO7I6j60T25Jfwtc5vYDImVIXHC1hSZ5kk7crsKoWZ1cvmXNgZZvOVIHu3KTMjZSIuaweBZ0Ph5i2CfiRgweMoK8MIQj47Q1bIKhxjlwCV3Srhj4Lx6mrchdmdVOvnGLcd+FZjlWEFxBkIo9ZOpwYx6fZaPi6QVtSTBVfltdcYoLOeTF/UFrV01XyvA==\",\n" +
                "    \"data\": \"6/jIhjEIGfIZAXmnQwqYbrbRTQa58B1N7+qJwXM+P3xrGLU5p2xbUBEjSrclgu5JQK1flSeptyKkVtACOyN/5lcNpCKeuf4=\"\n" +
                "}";
        EncryptModel decrypt = payloadAssistant.decrypt(text, EncryptModel.class);
        System.out.println("name = " + decrypt.getName());
        System.out.println("value = " + decrypt.getValue());
    }

    @Test
    void keyPair() {
        RsaKeyPairAssistant.KeyPairBase64 keyPairBase64 = RsaKeyPairAssistant.generateKeyPairBase64();
        System.out.println("keyPairBase64.rsaPrivateKey() = \n" + keyPairBase64.rsaPrivateKey());
        System.out.println("keyPairBase64.rsaPublicKey() = \n" + keyPairBase64.rsaPublicKey());

    }
}
