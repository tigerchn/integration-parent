package com.integration.common.redisson.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson 集成开关与可选调优，前缀 {@code integration.redisson}。
 * <p>连接地址、库号、密码等默认与 {@code spring.data.redis} 对齐，由 {@link IntegrationRedissonAutoConfiguration} 读取。
 */
@ConfigurationProperties(prefix = "integration.redisson")
public class IntegrationRedissonProperties {

    /**
     * 为 {@code true} 时注册 {@link org.redisson.api.RedissonClient} Bean；默认关闭以免未部署 Redis 时启动失败。
     */
    private boolean enabled = false;

    /**
     * Redisson 客户端名称，便于 Redis MONITOR / 服务端识别。
     */
    private String clientName = "integration-redisson";

    /**
     * 连接超时（毫秒）。
     */
    private int connectTimeoutMs = 10_000;

    /**
     * 命令响应超时（毫秒）。
     */
    private int timeoutMs = 3_000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
