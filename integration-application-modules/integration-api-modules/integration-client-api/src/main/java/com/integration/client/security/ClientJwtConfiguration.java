package com.integration.client.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(ClientSecurityProperties.class)
public class ClientJwtConfiguration {

    @Bean
    JwtEncoder clientJwtEncoder(ClientSecurityProperties properties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(hmacKey(properties.getJwt().getSecret())));
    }

    @Bean
    JwtDecoder clientJwtDecoder(ClientSecurityProperties properties) {
        return NimbusJwtDecoder.withSecretKey(hmacKey(properties.getJwt().getSecret()))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private static SecretKey hmacKey(String rawSecret) {
        byte[] bytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("integration.client.security.jwt.secret must be at least 32 bytes");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
