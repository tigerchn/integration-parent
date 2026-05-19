package com.integration.demo.lock;

import com.integration.common.redisson.lock.DistributedLock;
import org.springframework.stereotype.Service;

/**
 * 演示 {@link DistributedLock}：需在配置中开启 {@code integration.redisson.enabled=true} 且 Redis 可用时才会真正加锁。
 */
@Service
public class DemoDistributedLockService {

    /**
     * 对同一 {@code resourceKey} 并发调用时串行化；锁 key 由 SpEL 拼接。
     *
     * @param resourceKey 业务维度标识（示例中为路径参数）
     * @return 固定说明文案，便于联调观察
     */
    @DistributedLock(key = "'integration-demo:sample:' + #resourceKey", waitMillis = 5_000, leaseMillis = -1)
    public String runUnderLock(String resourceKey) {
        return "executed under distributed lock for key=" + resourceKey;
    }
}
