package com.integration.mqconsumer.web;

import com.integration.common.core.api.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mq")
public class MqConsumerPingController {

    @GetMapping("/ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("module", "integration-mq-consumer", "status", "UP"));
    }
}
