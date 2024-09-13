package com.backbase.accesscontrol.manager;

import com.backbase.accesscontrol.service.ConsumerControlService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@AllArgsConstructor
public class ConsumerManager {

    private final ConsumerControlService consumerControlService;
    private final AtomicBoolean isConsumerPaused = new AtomicBoolean(false);

    public void pauseAndResumeAfterDelay(Message<String> message, long delay) {
        if (!isConsumerPaused.get()) {
            log.info("Pausing consumer for retryable exception.");
            consumerControlService.controlConsumer(message, true);
            isConsumerPaused.set(true);

            // Schedule the resume action after the specified delay
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                log.info("Resuming consumer after delay.");
                consumerControlService.controlConsumer(message, false);
                isConsumerPaused.set(false);
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    public void resumeConsumer(Message<String> message) {
        if (isConsumerPaused.get()) {
            log.info("Resuming consumer manually.");
            consumerControlService.controlConsumer(message, false);
            isConsumerPaused.set(false);
        }
    }

}