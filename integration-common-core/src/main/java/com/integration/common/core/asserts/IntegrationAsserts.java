package com.integration.common.core.asserts;

import com.integration.common.core.exception.AssertionFailures;

import java.util.Objects;

/**
 * 轻量前置校验工具，失败时抛出 {@link IllegalArgumentException}。
 */
public final class IntegrationAsserts {

    private IntegrationAsserts() {
    }

    /**
     * 断言引用非空。
     *
     * @param value   待校验对象
     * @param message 失败消息
     */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw AssertionFailures.illegalArgument(message);
        }
    }

    /**
     * 断言布尔表达式为真。
     *
     * @param expression 条件
     * @param message      失败消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw AssertionFailures.illegalArgument(message);
        }
    }

    /**
     * 断言两个对象 {@link Objects#equals(Object, Object)} 相等。
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message    失败消息
     */
    public static void equals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw AssertionFailures.illegalArgument(message);
        }
    }
}
