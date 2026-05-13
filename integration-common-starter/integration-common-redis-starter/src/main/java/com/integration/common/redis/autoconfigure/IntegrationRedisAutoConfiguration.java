package com.integration.common.redis.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 集成自动配置：提供键为 String、值为 JSON 的 {@link RedisTemplate}。
 */
@AutoConfiguration(after = org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@ConditionalOnClass(RedisOperations.class)
public class IntegrationRedisAutoConfiguration {

    /**
     * @param factory Redis 连接工厂
     * @return 名为 {@code integrationRedisTemplate} 的模板，若未自定义则注册
     */
    @Bean(name = "integrationRedisTemplate")
    @ConditionalOnMissingBean(name = "integrationRedisTemplate")
    public RedisTemplate<String, Object> integrationRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
