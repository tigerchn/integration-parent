package com.integration.lockdemo.lock;

import com.integration.common.redisson.lock.DistributedLock;
import org.springframework.stereotype.Service;

/**
 * 在业务方法上使用 {@link DistributedLock}，供 Postman 观察串行化或抢锁失败。
 * <p>并发场景：{@code waitMillis} 必须<strong>小于</strong>临界区内持锁时间，后到的线程才会在超时后抛出
 * {@link com.integration.common.redisson.lock.DistributedLockAcquireException}；若 {@code waitMillis}
 * 大于持锁时间，后到的线程会一直等到锁释放并成功执行，不会抛异常。
 */
@Service
public class LockVerifyService {

    /** 临界区内持锁时长（毫秒），故意大于 {@link DistributedLock#waitMillis()} 以便并发第二笔抢锁失败。 */
    private static final int HOLD_LOCK_MILLIS = 10_000;

    @DistributedLock(
            key = "'integration-lock-demo:verify:' + #resourceKey",
            waitMillis = 2,
            leaseMillis = -1)
    public void transfer(String resourceKey) {
        try {
            Thread.sleep(HOLD_LOCK_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while holding lock", e);
        }
    }
}
