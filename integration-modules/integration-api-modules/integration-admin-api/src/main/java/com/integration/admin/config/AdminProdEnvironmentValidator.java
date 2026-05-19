package com.integration.admin.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 生产环境启动时校验必需配置，避免占位密钥或空环境变量静默上线。
 */
@Component
@Profile("prod")
public class AdminProdEnvironmentValidator implements ApplicationRunner {

    private static final int MIN_JWT_SECRET_LENGTH = 32;

    private final Environment environment;

    public AdminProdEnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        requireNonBlank("spring.datasource.url");
        requireNonBlank("spring.datasource.username");
        requireNonBlank("spring.datasource.password");
        requireNonBlank("spring.data.redis.host");

        String jwtSecret = requireNonBlank("integration.admin.security.jwt.secret");
        if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "integration.admin.security.jwt.secret 长度至少 " + MIN_JWT_SECRET_LENGTH + " 字符");
        }
        rejectDevPlaceholder(jwtSecret, "integration.admin.security.jwt.secret");

        if (isEncryptEnabled()) {
            requireNonBlank("integration.encrypt.rsa-public-key");
            if (isDecryptRequestEnabled()) {
                requireNonBlank("integration.encrypt.rsa-private-key");
            }
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
