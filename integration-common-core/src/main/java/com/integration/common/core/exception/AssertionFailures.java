package com.integration.common.core.exception;

/**
 * 断言失败时抛出 {@link IllegalArgumentException} 的工厂方法集合。
 */
public final class AssertionFailures {

    private AssertionFailures() {
    }

    /**
     * @param message 异常消息
     * @return 非法参数异常实例
     */
    public static IllegalArgumentException illegalArgument(String message) {
        return new IllegalArgumentException(message);
    }
}
