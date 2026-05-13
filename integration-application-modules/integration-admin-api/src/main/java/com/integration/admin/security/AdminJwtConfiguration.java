package com.integration.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 管理端 JWT 编解码 Bean：基于配置密钥的 HS256。
 */
@Configuration
@EnableConfigurationProperties(AdminSecurityProperties.class)
public class AdminJwtConfiguration {

    /** @return 签发 JWT 使用的 {@link JwtEncoder} */
    @Bean
    @Primary
    JwtEncoder jwtEncoder(AdminSecurityProperties properties) {
        SecretKey key = hmacKey(properties.getJwt().getSecret());
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    /** @return 校验 JWT 使用的 {@link JwtDecoder} */
    @Bean
    @Primary
    JwtDecoder jwtDecoder(AdminSecurityProperties properties) {
        SecretKey key = hmacKey(properties.getJwt().getSecret());
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /**
     * 由原始密钥字节构造 HMAC 密钥，长度不足 32 字节时抛出异常。
     */
    private static SecretKey hmacKey(String rawSecret) {
        byte[] bytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("integration.admin.security.jwt.secret must be at least 32 bytes");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
