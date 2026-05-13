package com.integration.common.redisson.autoconfigure;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * 可选 Redisson 客户端：仅在 {@code integration.redisson.enabled=true} 时注册 {@link RedissonClient}。
 * <p>单机模式复用 {@link RedisProperties}（与 Spring Data Redis 相同数据源配置）。
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "integration.redisson", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(IntegrationRedissonProperties.class)
public class IntegrationRedissonAutoConfiguration {

    /**
     * @param redisProperties Boot 标准 {@code spring.data.redis} 绑定
     * @param properties      本模块开关与超时等
     * @return 供分布式锁等使用的客户端，关闭容器时 {@link RedissonClient#shutdown()}
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisProperties redisProperties,
                                         IntegrationRedissonProperties properties) {
        Config config = new Config();
        SingleServerConfig server = config.useSingleServer();
        server.setAddress(buildAddress(redisProperties));
        if (StringUtils.hasText(redisProperties.getPassword())) {
            server.setPassword(redisProperties.getPassword());
        }
        if (StringUtils.hasText(redisProperties.getUsername())) {
            server.setUsername(redisProperties.getUsername());
        }
        server.setDatabase(redisProperties.getDatabase());
        server.setClientName(properties.getClientName());
        server.setConnectTimeout(properties.getConnectTimeoutMs());
        server.setTimeout(properties.getTimeoutMs());
        return Redisson.create(config);
    }

    private static String buildAddress(RedisProperties redisProperties) {
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        if (redisProperties.getSsl().isEnabled()) {
            return "rediss://" + host + ":" + port;
        }
        return "redis://" + host + ":" + port;
    }
}
