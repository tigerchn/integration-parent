package com.integration.client.security;

import com.integration.client.user.service.ClientUserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final List<SimpleGrantedAuthority> APP_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("ROLE_APP_USER"),
            new SimpleGrantedAuthority("app:user:read"));

    private final ClientUserService clientUserService;

    public ClientJwtAuthenticationConverter(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        clientUserService.requireEnabledUser(userId);
        return new JwtAuthenticationToken(jwt, APP_AUTHORITIES);
    }
}
