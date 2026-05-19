package com.integration.jobconsumer.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消费端存活心跳（本地定时任务示例）。
 */
@Component
public class JobHeartbeatScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobHeartbeatScheduler.class);

    @Scheduled(fixedDelayString = "${integration.job.scheduler.heartbeat-ms:60000}")
    public void heartbeat() {
        log.debug("job consumer scheduled heartbeat");
    }
}
