package com.integration.demo.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 在 Docker 可用时启动 Redis 容器，校验 {@link RedissonClient} 与分布式锁基本语义。
 * <p>无 Docker 环境时本类测试会被 Testcontainers 跳过。
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class DemoRedissonLockIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(6379)));
        registry.add("integration.redisson.enabled", () -> "true");
    }

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void tryLockUnlockRoundTrip() throws InterruptedException {
        assertThat(redissonClient).isNotNull();
        RLock lock = redissonClient.getLock("integration-demo:test:lock");
        assertThat(lock.tryLock(3, 10, TimeUnit.SECONDS)).as("首次加锁").isTrue();
        try {
            assertThat(lock.isHeldByCurrentThread()).isTrue();
        } finally {
            lock.unlock();
        }
        assertThat(lock.tryLock(3, 10, TimeUnit.SECONDS)).as("释放后应可再次加锁").isTrue();
        lock.unlock();
    }
}
