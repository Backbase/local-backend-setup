package com.backbase.datagroup.configuration;

import java.util.Collections;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerContainerRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

@Configuration
public class ListenerContainerCustomizerConfig {

    @Bean
    public ListenerContainerCustomizer<AbstractMessageListenerContainer<?, ?>> listenerContainerCustomizer(
        ListenerContainerRegistry registry) {

        return ((container, destinationName, group) -> container.setCommonErrorHandler(
            new DefaultErrorHandler(stopAllListenerContainerFallback(container, registry))));
    }

    private ConsumerRecordRecoverer stopAllListenerContainerFallback(MessageListenerContainer container,
        ListenerContainerRegistry registry) {

        return (consumerRecord, exception) -> {
            var containerStoppingErrorHandler = new CommonContainerStoppingErrorHandler();
            containerStoppingErrorHandler.handleRemaining(exception, Collections.emptyList(), new MockConsumer<>(
                OffsetResetStrategy.NONE), container);

            registry.getAllListenerContainers()
                .forEach(listenerContainer -> containerStoppingErrorHandler.handleRemaining(exception,
                    Collections.emptyList(), new MockConsumer<>(
                        OffsetResetStrategy.NONE), container));
        };
    }
}
