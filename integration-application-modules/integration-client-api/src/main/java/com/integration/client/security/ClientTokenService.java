package com.integration.client.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ClientTokenService {

    private final JwtEncoder jwtEncoder;
    private final ClientSecurityProperties securityProperties;

    public ClientTokenService(@Qualifier("clientJwtEncoder") JwtEncoder jwtEncoder,
                              ClientSecurityProperties securityProperties) {
        this.jwtEncoder = jwtEncoder;
        this.securityProperties = securityProperties;
    }

    public TokenIssueResult issueAccessToken(Long userId, String openid) {
        Instant now = Instant.now();
        long expiresInSeconds = securityProperties.getJwt().getExpiresInSeconds();
        Instant exp = now.plusSeconds(expiresInSeconds);
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("integration-client")
                .issuedAt(now)
                .expiresAt(exp)
                .subject(String.valueOf(userId))
                .id(jti)
                .claim("openid", openid)
                .claim("channel", "mini-program")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
        return new TokenIssueResult(jwt.getTokenValue(), expiresInSeconds);
    }

    public record TokenIssueResult(String accessToken, long expiresInSeconds) {
    }
}
