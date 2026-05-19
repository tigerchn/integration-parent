package com.integration.common.log.autoconfigure;

import com.integration.common.log.trace.TraceIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * 日志与链路相关自动配置（如 traceId 过滤器注册）。
 */
@AutoConfiguration
public class LogAutoConfiguration {

    /**
     * 注册 {@link TraceIdFilter}，优先级最高并匹配所有 URL。
     *
     * @return 过滤器注册 Bean
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>(new TraceIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
