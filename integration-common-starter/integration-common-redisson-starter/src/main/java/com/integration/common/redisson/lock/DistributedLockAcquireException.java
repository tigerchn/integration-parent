package com.integration.common.redisson.lock;

/**
 * 在配置的时间内未能取得分布式锁，或等待过程被中断时抛出。
 */
public class DistributedLockAcquireException extends RuntimeException {

    public DistributedLockAcquireException(String message) {
        super(message);
    }

    public DistributedLockAcquireException(String message, Throwable cause) {
        super(message, cause);
    }
}
