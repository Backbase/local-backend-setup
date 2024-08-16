package com.backbase.dbs.user.manager;

import com.backbase.dbs.user.manager.service.ConsumerControlService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsumerManager {

    private final AtomicBoolean isConsumerPaused = new AtomicBoolean(false);

    private ConsumerControlService consumerControlService;


    public void pauseConsumer(Message<String> message) {
        if (!isConsumerPaused.get()) {
            consumerControlService.controlConsumer(message, true);
            isConsumerPaused.set(true);
        }
    }

    public void resumeConsumer(Message<String> message) {
        if (isConsumerPaused.get()) {
            consumerControlService.controlConsumer(message, false);
            isConsumerPaused.set(false);
        }
    }

    public boolean isConsumerPaused() {
        return isConsumerPaused.get();
    }
}
