package com.integration.common.redisson.autoconfigure;

import com.integration.common.redisson.lock.DistributedLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 注册 {@link DistributedLockAspect}，仅在已存在 {@link RedissonClient} 时生效。
 */
@AutoConfiguration(after = IntegrationRedissonAutoConfiguration.class)
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnBean(RedissonClient.class)
public class IntegrationRedissonLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DistributedLockAspect.class)
    public DistributedLockAspect distributedLockAspect(RedissonClient redissonClient,
                                                       IntegrationRedissonProperties properties) {
        return new DistributedLockAspect(redissonClient, properties);
    }
}
