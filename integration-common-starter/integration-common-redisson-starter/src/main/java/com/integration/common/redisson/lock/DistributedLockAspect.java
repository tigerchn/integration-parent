package com.integration.common.redisson.lock;

import com.integration.common.redisson.autoconfigure.IntegrationRedissonProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 处理 {@link DistributedLock}：在存在 {@link RedissonClient} 时织入加锁逻辑。
 * <p>优先级高于常见事务切面，使锁尽量包裹在事务外层（仍建议业务入口显式分层）。
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final IntegrationRedissonProperties properties;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public DistributedLockAspect(RedissonClient redissonClient, IntegrationRedissonProperties properties) {
        this.redissonClient = redissonClient;
        this.properties = properties;
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = resolveLockKey(joinPoint, distributedLock);
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = tryAcquire(lock, distributedLock);
            if (!acquired) {
                throw new DistributedLockAcquireException("Failed to acquire distributed lock: " + lockKey);
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedLockAcquireException("Interrupted while acquiring distributed lock: " + lockKey, e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private boolean tryAcquire(RLock lock, DistributedLock distributedLock) throws InterruptedException {
        long waitMillis = distributedLock.waitMillis();
        long leaseMillis = distributedLock.leaseMillis();
        if (leaseMillis < 0) {
            return lock.tryLock(waitMillis, TimeUnit.MILLISECONDS);
        }
        return lock.tryLock(waitMillis, leaseMillis, TimeUnit.MILLISECONDS);
    }

    private String resolveLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String expression = distributedLock.key();
        if (!StringUtils.hasText(expression)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String prefix = properties.getLock().getKeyPrefix();
            return prefix + ":" + signature.getDeclaringTypeName() + ":" + signature.getName();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        EvaluationContext context = buildContext(joinPoint.getTarget(), method, args);
        Object value = expressionParser.parseExpression(expression).getValue(context);
        if (value == null) {
            throw new IllegalStateException("@DistributedLock SpEL evaluated to null for expression: " + expression);
        }
        return value.toString();
    }

    private EvaluationContext buildContext(Object target, Method method, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext(target);
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        for (int i = 0; i < args.length; i++) {
            context.setVariable("a" + i, args[i]);
            context.setVariable("p" + i, args[i]);
        }
        return context;
    }
}
