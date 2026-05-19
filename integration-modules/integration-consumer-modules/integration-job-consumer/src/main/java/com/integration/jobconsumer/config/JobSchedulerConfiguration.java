package com.integration.jobconsumer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JobSchedulerProperties.class)
public class JobSchedulerConfiguration {
}
