package com.integration.common.redisson.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在方法执行前后通过 Redisson 获取与释放分布式锁。
 * <p>需存在 {@link org.redisson.api.RedissonClient} Bean（通常要求 {@code integration.redisson.enabled=true}）。
 * <p>与 {@code @Transactional} 同时使用时，建议将本注解与事务均作用于对外入口方法，避免同类自调用导致代理不生效。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁 key 的 SpEL 表达式；为空时使用 {@code integration.redisson.lock.key-prefix}、声明类全名与方法名拼接默认 key。
     * <p>可引用方法参数名（需编译带 {@code -parameters}）或使用 {@code #p0}、{@code #a0} 等形式。
     */
    String key() default "";

    /**
     * 最长等待获取锁的时间（毫秒）。{@code 0} 表示仅尝试一次（由 Redisson 语义决定）。
     */
    long waitMillis() default 10000L;

    /**
     * 持锁租约（毫秒）。小于 {@code 0} 时使用 {@link org.redisson.api.RLock#tryLock(long, java.util.concurrent.TimeUnit)}，
     * 由 Redisson 看门狗续期直至当前线程 {@code unlock}。
     */
    long leaseMillis() default -1L;
}
