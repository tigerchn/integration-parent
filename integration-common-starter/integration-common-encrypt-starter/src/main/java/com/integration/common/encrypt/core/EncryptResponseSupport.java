package com.integration.common.encrypt.core;

import com.integration.common.encrypt.config.EncryptPathSupport;
import com.integration.common.encrypt.config.EncryptProperties;

/**
 * 响应体是否按配置加密（与 {@link EncryptResponseBodyAdvice} 路径规则一致）。
 */
public final class EncryptResponseSupport {

    private EncryptResponseSupport() {
    }

    public static boolean shouldEncryptResponse(EncryptProperties properties, String path) {
        if (!properties.isEnable() || !properties.isEncryptResponseBodyEnabled()) {
            return false;
        }
        if (EncryptPathSupport.matchesExclude(path, properties.getExcludePaths())) {
            return false;
        }
        return EncryptPathSupport.matchesIncludeOrAll(path, properties.getEncryptResponsePathPatterns());
    }
}
