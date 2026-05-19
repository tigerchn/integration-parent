package com.integration.admin.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 管理端访问令牌签发：封装 subject、jti、username 等标准声明。
 */
@Service
public class AdminTokenService {

    private final JwtEncoder jwtEncoder;
    private final AdminSecurityProperties securityProperties;

    /**
     * @param jwtEncoder          JWT 编码器
     * @param securityProperties  过期时间等安全配置
     */
    public AdminTokenService(JwtEncoder jwtEncoder, AdminSecurityProperties securityProperties) {
        this.jwtEncoder = jwtEncoder;
        this.securityProperties = securityProperties;
    }

    /**
     * 为用户签发访问令牌。
     *
     * @param userId   用户主键，写入 subject
     * @param username 用户名，写入自定义 claim
     * @return 令牌串与过期秒数
     */
    public TokenIssueResult issueAccessToken(Long userId, String username) {
        Instant now = Instant.now();
        long expiresInSeconds = securityProperties.getJwt().getExpiresInSeconds();
        Instant exp = now.plusSeconds(expiresInSeconds);
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("integration-admin")
                .issuedAt(now)
                .expiresAt(exp)
                .subject(String.valueOf(userId))
                .id(jti)
                .claim("username", username)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
        return new TokenIssueResult(jwt.getTokenValue(), expiresInSeconds);
    }

    /** 签发结果：访问令牌与过期时间（秒） */
    public record TokenIssueResult(String accessToken, long expiresInSeconds) {
    }
}
