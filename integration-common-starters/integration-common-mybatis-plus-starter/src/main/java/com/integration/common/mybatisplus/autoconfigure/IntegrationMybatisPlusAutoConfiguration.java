package com.integration.common.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis-Plus 插件自动配置（分页等）。
 */
@AutoConfiguration
@ConditionalOnClass(MybatisPlusInterceptor.class)
@EnableConfigurationProperties(MybatisPlusIntegrationProperties.class)
public class IntegrationMybatisPlusAutoConfiguration {

    /**
     * @param properties 分页等开关与方言配置
     * @return MyBatis-Plus 拦截器链
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusIntegrationProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (properties.isPaginationEnabled()) {
            PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(properties.getDbType());
            pagination.setOverflow(properties.isOverflow());
            interceptor.addInnerInterceptor(pagination);
        }
        return interceptor;
    }
}
