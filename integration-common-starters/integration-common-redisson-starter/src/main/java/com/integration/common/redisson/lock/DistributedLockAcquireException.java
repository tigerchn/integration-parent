package com.integration.common.redisson.lock;

import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.IntegrationException;

/**
 * 在配置的时间内未能取得分布式锁，或等待过程被中断时抛出。
 */
public class DistributedLockAcquireException extends IntegrationException {

    public DistributedLockAcquireException(String message) {
        super(ResultCode.CONFLICT, message);
    }

    public DistributedLockAcquireException(String message, Throwable cause) {
        super(ResultCode.CONFLICT, message, cause);
    }
}
