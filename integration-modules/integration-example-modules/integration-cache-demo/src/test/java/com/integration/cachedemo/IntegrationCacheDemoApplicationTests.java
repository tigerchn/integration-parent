package com.integration.cachedemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.cachedemo.dto.CacheEchoPayload;
import com.integration.cachedemo.service.CacheDevService;
import com.integration.cachedemo.service.CacheTestService;
import com.integration.common.cache.inspect.CacheInspectUtil;
import com.integration.common.cache.inspect.CacheRegionSnapshot;
import com.integration.common.core.api.ApiResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.List;

@SpringBootTest(properties = "integration.cache.enabled=true")
class IntegrationCacheDemoApplicationTests {


    @Resource
    CacheManager cacheManager;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    CacheTestService cacheTestService;

    @Resource
    CacheDevService cacheDevService;

    @Test
    void contextLoads() throws JsonProcessingException {
        // 所有数据
        int sampleLimit = Math.max(0, Math.min(10, 500));
        List<CacheRegionSnapshot> cacheList = CacheInspectUtil.inspectAll(cacheManager, sampleLimit);
        System.out.println("cacheList = " + cacheList.size());

        if (!cacheList.isEmpty()) {
            for (CacheRegionSnapshot cache : cacheList) {
                System.out.println("cache = " + objectMapper.writeValueAsString(cache));
            }

            return;
        }


        // 添加开发数据
        CacheEchoPayload local_dev_key_1 = cacheDevService.echo("local_dev_key_1");
        CacheEchoPayload local_dev_key_2 = cacheDevService.echo("local_dev_key_2");
        CacheEchoPayload local_dev_key_3 = cacheDevService.echo("local_dev_key_3");

        // 添加测试数据
        CacheEchoPayload local_test_key_1 = cacheTestService.echo("local_test_key_1");
        CacheEchoPayload local_test_key_2 = cacheTestService.echo("local_test_key_2");
        CacheEchoPayload local_test_key_3 = cacheTestService.echo("local_test_key_3");

        cacheList = CacheInspectUtil.inspectAll(cacheManager, sampleLimit);


        System.out.println("cacheList = " + objectMapper.writeValueAsString(cacheList));

        System.out.println("数量 = " + getSum(cacheList));
        cacheDevService.evict("local_dev_key_1");

        cacheList = CacheInspectUtil.inspectAll(cacheManager, sampleLimit);
        System.out.println("数量 = " + getSum(cacheList));



    }

    private int getSum(List<CacheRegionSnapshot> cacheList){
        int sum = 0;
        for (CacheRegionSnapshot cacheRegionSnapshot : cacheList) {
            sum += cacheRegionSnapshot.entries().size();
        }
        return sum;
    }


}
