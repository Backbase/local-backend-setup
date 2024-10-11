package com.backbase.accesscontrol.configuration;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerPausingBackOffHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerContainerPauseService;
import org.springframework.kafka.listener.ListenerContainerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.model.TopicConfig;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class KafkaListenerCustomizerConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;
    private final ListenerContainerRegistry registry;
    private final TaskScheduler scheduler;

    @Bean
    public ListenerContainerWithDlqAndRetryCustomizer customizer() {
        return new ListenerContainerWithDlqAndRetryCustomizer() {

            @Override
            public void configure(AbstractMessageListenerContainer<?, ?> container, String destinationName,
                                  String group, @Nullable
                                  BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
                                  @Nullable BackOff backOff) {

                Map<String, TopicConfig> topicConfigs = getTopicConfigurations();

                // Fetch configuration for the current destination (topic)
                TopicConfig topicConfig = topicConfigs.getOrDefault(destinationName, getDefaultTopicConfig());

                // Custom DeadLetterPublishingRecoverer with dynamic DLQ topic resolution
                ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                    (consumerRecord, exception) -> {
                        String dlqTopic = topicConfig.getDlqTopic();
                        log.info("Sending message to DLQ with custom headers for topic {}", consumerRecord.topic());

                        // Send message to DLQ with dynamic topic
                        return new TopicPartition(dlqTopic, consumerRecord.partition());
                    });

                // Create a DefaultErrorHandler with custom backoff and retries
                DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, topicConfig.getBackOff(),
                          new ContainerPausingBackOffHandler(new ListenerContainerPauseService(registry, scheduler)));

                // Mark non-retryable exceptions
                errorHandler.addNotRetryableExceptions(PayloadParsingException.class);
                errorHandler.addNotRetryableExceptions(NotFoundException.class);
                errorHandler.addNotRetryableExceptions(BadRequestException.class);

                container.setCommonErrorHandler(errorHandler);
            }

            @Override
            public boolean retryAndDlqInBinding(String destinationName, String group) {
                // Disable Spring Cloud Stream's default retry and DLQ handling for topics we manage in custom config
                return !getTopicConfigurations().containsKey(destinationName);
            }
        };
    }

    // Dynamically manage topic-specific configurations (backoff, retries, DLQ topics)
    private Map<String, TopicConfig> getTopicConfigurations() {
        Map<String, TopicConfig> topicConfigs = new HashMap<>();

        // Set UNLIMITED_ATTEMPTS if retryAttempts is -1
        long dataGroupRetryAttempts = rchKafkaGenericProperties.getUpsertDataGroupRetryAttempts() == -1
            ? FixedBackOff.UNLIMITED_ATTEMPTS
            : rchKafkaGenericProperties.getUpsertDataGroupRetryAttempts();

        topicConfigs.put(rchKafkaGenericProperties.getUpsertDataGroupTopicName(),
            new TopicConfig(rchKafkaGenericProperties.getUpsertDataGroupErrorTopicName(),
                new FixedBackOff(rchKafkaGenericProperties.getUpsertDataGroupBackOffDelay(), dataGroupRetryAttempts)));

        return topicConfigs;
    }

    // Configurable default topic configuration in case no specific config is found
    private TopicConfig getDefaultTopicConfig() {
        // Use configurable default values from properties
        return new TopicConfig(
            rchKafkaGenericProperties.getDefaultDlqTopicName(),
            new FixedBackOff(rchKafkaGenericProperties.getDefaultBackOffDelay(),
                rchKafkaGenericProperties.getDefaultRetryAttempts())
        );
    }
}