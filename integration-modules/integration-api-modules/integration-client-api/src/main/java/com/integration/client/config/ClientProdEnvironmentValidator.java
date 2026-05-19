package com.integration.client.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 生产环境启动时校验必需配置。
 */
@Component
@Profile("prod")
public class ClientProdEnvironmentValidator implements ApplicationRunner {

    private static final int MIN_JWT_SECRET_LENGTH = 32;

    private final Environment environment;

    public ClientProdEnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        requireNonBlank("spring.datasource.url");
        requireNonBlank("spring.datasource.username");
        requireNonBlank("spring.datasource.password");
        requireNonBlank("spring.data.redis.host");

        String jwtSecret = requireNonBlank("integration.client.security.jwt.secret");
        if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "integration.client.security.jwt.secret 长度至少 " + MIN_JWT_SECRET_LENGTH + " 字符");
        }
        rejectDevPlaceholder(jwtSecret, "integration.client.security.jwt.secret");

        boolean mock = environment.getProperty(
                "integration.client.wechat.mini-program.mock-enabled", Boolean.class, false);
        if (!mock) {
            requireNonBlank("integration.client.wechat.mini-program.app-id");
            requireNonBlank("integration.client.wechat.mini-program.app-secret");
        }

        if (isEncryptEnabled() && isDecryptRequestEnabled()) {
            requireNonBlank("integration.encrypt.rsa-public-key");
            requireNonBlank("integration.encrypt.rsa-private-key");
        }
    }

    private boolean isEncryptEnabled() {
        return environment.getProperty("integration.encrypt.enable", Boolean.class, false);
    }

    private boolean isDecryptRequestEnabled() {
        return environment.getProperty("integration.encrypt.decrypt-request-body-enabled", Boolean.class, false);
    }

    private String requireNonBlank(String key) {
        String value = environment.getProperty(key);
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("生产环境缺少必需配置: " + key);
        }
        return value.trim();
    }

    private void rejectDevPlaceholder(String value, String key) {
        if (value.contains("change-me")) {
            throw new IllegalStateException("生产环境禁止使用开发占位密钥: " + key);
        }
    }
}
