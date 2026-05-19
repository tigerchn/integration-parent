package com.integration.mqconsumer.mq;

/**
 * RabbitMQ 异步任务消息体（与生产者约定字段）。
 */
public record JobTaskMessage(String taskType, String payload, Long createdAt) {
}
