package com.backbase.accesscontrol.configuration;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.serializer.SafeJsonDeserializer;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@AllArgsConstructor
public class KafkaConsumerConfig {

    private final RchKafkaGenericProperties rchKafkaGenericProperties;
    private final DeadLetterPublishingRecoverer deadLetterPublishingRecoverer;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, rchKafkaGenericProperties.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, rchKafkaGenericProperties.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SafeJsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(4);  // Increase concurrency to allow parallel processing of partitions

        // Remove manual acknowledgment mode to rely on Kafka's auto-commit
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // REMOVE this line

        // Set the error handler to handle exceptions and send to DLQ if needed
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff =
            new FixedBackOff(1000L, 5); // Infinite retries with 1 sec delay

        // Create the DefaultErrorHandler with DeadLetterPublishingRecoverer and BackOff
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);

        // Mark ParsingPayloadException as non-retryable so it goes directly to the DLQ
        errorHandler.addNotRetryableExceptions(PayloadParsingException.class);

        return errorHandler;
    }
}



