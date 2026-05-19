package com.integration.common.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.util.StringUtils;

/**
 * 当 {@code integration.security.enabled=true} 时注册一条无状态 {@link SecurityFilterChain}；
 * 关闭或未配置时本自动配置不生效，由应用自行提供 Spring Security。
 */
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(IntegrationSecurityProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "integration.security", name = "enabled", havingValue = "true")
public class IntegrationSecurityAutoConfiguration {

    /**
     * @param http       HTTP 安全配置器
     * @param properties 放行路径与 JWT issuer 等
     * @return 集成安全过滤器链
     */
    @Bean
    @Order(100)
    SecurityFilterChain integrationSecurityFilterChain(HttpSecurity http, IntegrationSecurityProperties properties)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        String issuerUri = properties.getOauth2().getResourceServer().getJwt().getIssuerUri();
        if (StringUtils.hasText(issuerUri)) {
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                    jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri))));
        }

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(properties.getPermitAllPatterns().toArray(String[]::new)).permitAll();
            auth.anyRequest().authenticated();
        });

        return http.build();
    }
}
