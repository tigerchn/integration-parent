package com.integration.common.storage.autoconfigure.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ConfigurationCondition;

/**
 * {@code integration.storage.enabled=true} 且 {@code integration.storage.type=oss}。
 */
public class OnIntegrationOssStorage extends AllNestedConditions {

    public OnIntegrationOssStorage() {
        super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "integration.storage", name = "enabled", havingValue = "true")
    static class StorageEnabled {
    }

    @ConditionalOnProperty(prefix = "integration.storage", name = "type", havingValue = "oss")
    static class OssType {
    }
}
