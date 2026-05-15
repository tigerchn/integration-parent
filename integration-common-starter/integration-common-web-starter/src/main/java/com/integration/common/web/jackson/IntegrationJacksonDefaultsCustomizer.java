package com.integration.common.web.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.TimeZone;

/**
 * 平台级 Jackson 默认：与 Spring Boot 提供的 {@link ObjectMapper} Bean 共用同一套
 * {@link Jackson2ObjectMapperBuilder}，保证 Web、注入点、{@code Jsons} 等序列化行为一致。
 * <p>
 * 由 {@link com.integration.common.web.autoconfigure.JacksonDefaultsAutoConfiguration} 以
 * 高优先级 {@code @Order} 注册，便于业务工程再注册 {@link Jackson2ObjectMapperBuilderCustomizer} 覆盖同一属性。
 */
public class IntegrationJacksonDefaultsCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    static final String DEFAULT_TIME_ZONE_ID = "Asia/Shanghai";

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.timeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE_ID));
        builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
