package com.integration.common.security.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用安全 starter 配置项，前缀 {@code integration.security}。
 */
@ConfigurationProperties(prefix = "integration.security")
public class IntegrationSecurityProperties {

    /**
     * 为 {@code true} 时由 {@link IntegrationSecurityAutoConfiguration} 注册 {@link org.springframework.security.web.SecurityFilterChain}；
     * 为 {@code false} 或未配置则跳过本 starter 的安全 Bean，由应用自行配置。
     */
    private boolean enabled = false;

    private List<String> permitAllPatterns = new ArrayList<>(List.of(
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/doc.html",
            "/webjars/**"
    ));

    @NestedConfigurationProperty
    private OAuth2 oauth2 = new OAuth2();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPermitAllPatterns() {
        return permitAllPatterns;
    }

    public void setPermitAllPatterns(List<String> permitAllPatterns) {
        this.permitAllPatterns = permitAllPatterns;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }

    public void setOauth2(OAuth2 oauth2) {
        this.oauth2 = oauth2;
    }

    public static class OAuth2 {

        @NestedConfigurationProperty
        private ResourceServer resourceServer = new ResourceServer();

        public ResourceServer getResourceServer() {
            return resourceServer;
        }

        public void setResourceServer(ResourceServer resourceServer) {
            this.resourceServer = resourceServer;
        }
    }

    public static class ResourceServer {

        @NestedConfigurationProperty
        private Jwt jwt = new Jwt();

        public Jwt getJwt() {
            return jwt;
        }

        public void setJwt(Jwt jwt) {
            this.jwt = jwt;
        }
    }

    public static class Jwt {

        /**
         * 非空时启用 OAuth2 Resource Server 的 JWT 校验（issuer-uri）。
         */
        private String issuerUri;

        public String getIssuerUri() {
            return issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }
    }
}
