package com.integration.common.storage.autoconfigure;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.integration.common.storage.StorageService;
import com.integration.common.storage.autoconfigure.condition.OnIntegrationOssStorage;
import com.integration.common.storage.oss.AliyunOssStorageService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.util.StringUtils;

/**
 * {@code integration.storage.enabled=true} 且 {@code type=oss} 时注册 OSS 与 {@link StorageService}。
 */
@AutoConfiguration
@ConditionalOnClass(OSS.class)
@Conditional(OnIntegrationOssStorage.class)
@EnableConfigurationProperties(IntegrationStorageProperties.class)
public class AliyunOssStorageAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(OSS.class)
    public OSS ossClient(IntegrationStorageProperties properties) {
        IntegrationStorageProperties.Oss oss = properties.getOss();
        requireText(oss.getEndpoint(), "integration.storage.oss.endpoint");
        requireText(oss.getAccessKeyId(), "integration.storage.oss.access-key-id");
        requireText(oss.getAccessKeySecret(), "integration.storage.oss.access-key-secret");
        requireText(oss.getBucket(), "integration.storage.oss.bucket");
        return new OSSClientBuilder().build(
                oss.getEndpoint().trim(),
                oss.getAccessKeyId().trim(),
                oss.getAccessKeySecret().trim());
    }

    @Bean
    @ConditionalOnMissingBean(StorageService.class)
    public StorageService storageService(OSS ossClient, IntegrationStorageProperties properties) {
        return new AliyunOssStorageService(
                ossClient, properties.getOss().getBucket(), properties.getOss().getEndpoint());
    }

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Missing required property: " + name);
        }
    }
}
