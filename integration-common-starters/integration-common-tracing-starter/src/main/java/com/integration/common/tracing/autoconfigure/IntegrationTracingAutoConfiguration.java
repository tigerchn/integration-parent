package com.integration.common.tracing.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 引入 Brave 与 Zipkin 等依赖后的占位自动配置；
 * 采样与上报通过标准 {@code management.tracing.*}、{@code management.zipkin.tracing.*} 调整。
 */
@AutoConfiguration
public class IntegrationTracingAutoConfiguration {
}
