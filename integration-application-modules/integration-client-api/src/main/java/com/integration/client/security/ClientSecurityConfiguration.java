package com.integration.client.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.trace.TraceConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ClientSecurityConfiguration {

    private static final String[] PERMIT_ALL = {
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/doc.html",
            "/webjars/**",
            "/api/app/ping",
            "/api/app/open/**",
            "/api/app/auth/wx-login"
    };

    @Bean
    @Order(0)
    SecurityFilterChain clientSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("clientJwtDecoder") JwtDecoder jwtDecoder,
            ClientJwtAuthenticationConverter jwtAuthenticationConverter,
            ObjectMapper objectMapper,
            ClientTokenBlacklistService blacklistService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(reg -> reg.requestMatchers(PERMIT_ALL).permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter::convert)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeApiError(response, objectMapper, HttpStatus.UNAUTHORIZED, ResultCode.UNAUTHORIZED,
                                        ClientSecurityErrorHints.unauthorized(request.getRequestURI(), authException)))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeApiError(response, objectMapper, HttpStatus.FORBIDDEN, ResultCode.FORBIDDEN,
                                        ClientSecurityErrorHints.forbidden(request.getRequestURI(), accessDeniedException))));

        http.addFilterAfter(new ClientTokenBlacklistFilter(blacklistService), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    private static void writeApiError(HttpServletResponse response, ObjectMapper objectMapper, HttpStatus httpStatus,
                                      ResultCode resultCode, String message) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResult<Void> body = ApiResult.fail(resultCode, message);
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
