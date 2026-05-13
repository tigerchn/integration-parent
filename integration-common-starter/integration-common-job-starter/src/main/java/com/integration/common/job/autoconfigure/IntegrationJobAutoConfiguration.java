package com.integration.common.job.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务自动配置：启用 Spring {@link org.springframework.scheduling.annotation.Scheduled} 调度。
 */
@AutoConfiguration
@EnableScheduling
public class IntegrationJobAutoConfiguration {
}
