package com.integration.mqconsumer.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "integration.mq.consumer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JobTaskMqConsumer {

    private static final Logger log = LoggerFactory.getLogger(JobTaskMqConsumer.class);

    @RabbitListener(queues = "${integration.mq.consumer.queue}")
    public void onJobTask(JobTaskMessage message) {
        log.info("Consumed job task: type={}, payload={}, createdAt={}",
                message.taskType(), message.payload(), message.createdAt());
    }
}
