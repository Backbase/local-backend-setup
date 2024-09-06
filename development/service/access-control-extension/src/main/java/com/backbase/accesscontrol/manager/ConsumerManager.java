package com.backbase.accesscontrol.manager;

import com.backbase.accesscontrol.service.ConsumerControlService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class ConsumerManager {

    private final AtomicBoolean isConsumerPaused = new AtomicBoolean(false);
    private final ConsumerControlService consumerControlService;

    public void pauseConsumer(Message<?> message) {
        if (!isConsumerPaused.get()) {
            consumerControlService.controlConsumer(message, true);
            isConsumerPaused.set(true);
            log.info("Consumer paused for message: {}", message);
        }
    }

    public void resumeConsumer(Message<?> message) {
        if (isConsumerPaused.get()) {
            consumerControlService.controlConsumer(message, false);
            isConsumerPaused.set(false);
            log.info("Consumer resumed for message: {}", message);
        }
    }

    // New Method for Automatic Resuming after a delay
    @Async
    public void pauseAndResumeAfterDelay(Message<?> message, long delayMillis) {
        pauseConsumer(message);  // Pause the consumer

        try {
            Thread.sleep(delayMillis);  // Simulate a delay before resuming
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        resumeConsumer(message);  // Resume the consumer after delay
    }

    public boolean isConsumerPaused() {
        return isConsumerPaused.get();
    }
}