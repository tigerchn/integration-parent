package com.integration.demo.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 默认 test profile 关闭 Redisson 与 Redis 自动配置，验证不连 Redis 时上下文可启动且无 {@link RedissonClient} Bean。
 */
@SpringBootTest
@ActiveProfiles("test")
class DemoRedissonDisabledConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void redissonClientNotRegisteredWhenDisabled() {
        assertThat(applicationContext.getBeanNamesForType(RedissonClient.class)).isEmpty();
    }
}
