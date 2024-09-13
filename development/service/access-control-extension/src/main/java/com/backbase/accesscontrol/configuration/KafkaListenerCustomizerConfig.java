package com.backbase.accesscontrol.configuration;

import com.backbase.accesscontrol.constant.KafkaConstants;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@AllArgsConstructor
public class KafkaListenerCustomizerConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public ListenerContainerWithDlqAndRetryCustomizer customizer() {
        return new ListenerContainerWithDlqAndRetryCustomizer() {

            @Override
            public void configure(AbstractMessageListenerContainer<?, ?> container, String destinationName,
                                  String group,
                                  @Nullable
                                  BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> dlqDestinationResolver,
                                  @Nullable BackOff backOff) {

                // Custom DeadLetterPublishingRecoverer with dynamic DLQ topic resolution
                ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                    (record, exception) -> {
                        String dlqTopic = resolveDlqTopic(record.topic());
                        log.info("Sending message to DLQ with custom headers for topic {}", record.topic());

                        // Add custom headers for error information
                        record.headers()
                            .add(KafkaConstants.ERROR_CODE_HEADER, exception.getClass().getSimpleName().getBytes());
                        record.headers().add(KafkaConstants.ERROR_MESSAGE_HEADER, exception.getMessage().getBytes());
                        record.headers()
                            .add(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(exception).getBytes());

                        // Send message to DLQ with dynamic topic
                        return new TopicPartition(dlqTopic, record.partition());
                    });

                // Customize error handling for specific topics
                if (destinationName.equals("rch-30730-kep-data-group")) {
                    FixedBackOff retryBackOff = new FixedBackOff(1000L, 3L); // 3 retries, 1-second delay
                    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, retryBackOff);

                    // Mark PayloadParsingException as non-retryable
                    errorHandler.addNotRetryableExceptions(PayloadParsingException.class);
                    container.setCommonErrorHandler(errorHandler);
                }
            }

            @Override
            public boolean retryAndDlqInBinding(String destinationName, String group) {
                // Disable Spring Cloud Stream's default retry and DLQ handling for this topic
                return !destinationName.equals("rch-30730-kep-data-group");
            }
        };
    }

    // Utility method for dynamically resolving the DLQ topic name based on the source topic
    private String resolveDlqTopic(String originalTopic) {
        switch (originalTopic) {
            case "rch-30730-kep-data-group":
                return "rch-30730-kep-data-group-dlt";  // DLQ for this topic
            case "another-topic":
                return "another-topic-dlt";  // DLQ for another topic
            default:
                return "generic-dlt";  // Default DLQ if not explicitly mapped
        }
    }

    // Utility method for extracting stack trace
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}