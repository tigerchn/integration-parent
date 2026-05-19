package com.integration.client.wechat;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeChatMiniProgramProperties.class)
public class WeChatClientConfiguration {
}
