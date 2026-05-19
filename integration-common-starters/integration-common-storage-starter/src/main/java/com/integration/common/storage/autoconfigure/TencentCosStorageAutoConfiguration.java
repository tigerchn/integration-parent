package com.integration.common.storage.autoconfigure;

import com.integration.common.storage.StorageService;
import com.integration.common.storage.autoconfigure.condition.OnIntegrationCosStorage;
import com.integration.common.storage.cos.TencentCosStorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.util.StringUtils;

/**
 * {@code integration.storage.enabled=true} 且 {@code type=cos} 时注册 COS 与 {@link StorageService}。
 */
@AutoConfiguration
@ConditionalOnClass(COSClient.class)
@Conditional(OnIntegrationCosStorage.class)
@EnableConfigurationProperties(IntegrationStorageProperties.class)
public class TencentCosStorageAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(COSClient.class)
    public COSClient cosClient(IntegrationStorageProperties properties) {
        IntegrationStorageProperties.Cos cos = properties.getCos();
        requireText(cos.getSecretId(), "integration.storage.cos.secret-id");
        requireText(cos.getSecretKey(), "integration.storage.cos.secret-key");
        requireText(cos.getRegion(), "integration.storage.cos.region");
        requireText(cos.getBucket(), "integration.storage.cos.bucket");
        COSCredentials cred = new BasicCOSCredentials(cos.getSecretId(), cos.getSecretKey());
        ClientConfig cfg = new ClientConfig(new Region(cos.getRegion()));
        return new COSClient(cred, cfg);
    }

    @Bean
    @ConditionalOnMissingBean(StorageService.class)
    public StorageService storageService(COSClient cosClient, IntegrationStorageProperties properties) {
        return new TencentCosStorageService(cosClient, properties.getCos().getBucket());
    }

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Missing required property: " + name);
        }
    }
}
