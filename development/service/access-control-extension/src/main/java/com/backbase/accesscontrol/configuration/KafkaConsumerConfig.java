package com.backbase.accesscontrol.configuration;

import com.backbase.accesscontrol.constant.KafkaConstants;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.serializer.SafeJsonDeserializer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;

@Configuration
@AllArgsConstructor
public class KafkaConsumerConfig {

    private final RchKafkaGenericProperties rchKafkaGenericProperties;

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
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        // DeadLetterPublishingRecoverer for "Fail Fast" errors like ParsingPayloadException
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, exception) -> {
                // Add custom headers to the error record
                record.headers().add(KafkaConstants.ERROR_CODE_HEADER, exception.getClass().getSimpleName().getBytes());
                record.headers().add(KafkaConstants.ERROR_MESSAGE_HEADER, exception.getMessage().getBytes());
                record.headers().add(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(exception).getBytes());

                return new TopicPartition(rchKafkaGenericProperties.getUpsertDataGroupErrorTopicName(),
                    record.partition());
            }
        );

        // Create DefaultErrorHandler with no backoff strategy, relying on Kafka's retry mechanism
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);

        // Handle ParsingPayloadException and DeserializationException as non-retryable
        errorHandler.addNotRetryableExceptions(PayloadParsingException.class, DeserializationException.class);

        return errorHandler;
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, rchKafkaGenericProperties.getBootstrapServer());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


}



