package com.integration.demo.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 演示用定时任务：按配置间隔输出调试心跳日志。
 */
@Component
public class DemoHeartbeatJob {

    private static final Logger log = LoggerFactory.getLogger(DemoHeartbeatJob.class);

    /**
     * 按 {@code integration.demo.job-heartbeat-ms} 间隔输出调试级心跳日志。
     */
    @Scheduled(fixedDelayString = "${integration.demo.job-heartbeat-ms:60000}")
    public void tick() {
        log.debug("demo scheduled heartbeat");
    }
}
