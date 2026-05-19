package com.integration.jobconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.job.scheduler")
public class JobSchedulerProperties {

    /**
     * 本地定时心跳间隔（毫秒），见 {@link com.integration.jobconsumer.job.JobHeartbeatScheduler}。
     */
    private long heartbeatMs = 60_000L;

    public long getHeartbeatMs() {
        return heartbeatMs;
    }

    public void setHeartbeatMs(long heartbeatMs) {
        this.heartbeatMs = heartbeatMs;
    }
}
