package com.integration.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.trace.TraceConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 管理端 Spring Security：JWT 资源服务器、匿名放行路径、黑名单过滤器与认证管理器。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AdminSecurityConfiguration {

    private static final String[] PERMIT_ALL = {
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/doc.html",
            "/webjars/**",
            "/api/admin/auth/login",
            "/api/admin/open/**"
    };

    /**
     * 管理端主安全过滤器链：无状态会话、JWT 校验、401/403 JSON 响应及登出黑名单。
     */
    @Bean
    @Order(0)
    SecurityFilterChain adminSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            AdminJwtAuthenticationConverter adminJwtAuthenticationConverter,
            ObjectMapper objectMapper,
            AdminTokenBlacklistService blacklistService
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(reg -> reg.requestMatchers(PERMIT_ALL).permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(adminJwtAuthenticationConverter::convert)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeApiError(response, objectMapper, HttpStatus.UNAUTHORIZED, ResultCode.UNAUTHORIZED,
                                        AdminSecurityErrorHints.unauthorized(request.getRequestURI(), authException)))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeApiError(response, objectMapper, HttpStatus.FORBIDDEN, ResultCode.FORBIDDEN,
                                        AdminSecurityErrorHints.forbidden(request.getRequestURI(), accessDeniedException))));

        http.addFilterAfter(new AdminTokenBlacklistFilter(blacklistService), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 写入 JSON 格式的 {@link ApiResult} 错误体（含 traceId，与全局异常处理一致）。
     */
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

    /** @return 管理端密码编码器（BCrypt） */
    @Bean
    PasswordEncoder adminPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** @return 用于表单/用户名密码认证的 {@link AuthenticationManager} */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
