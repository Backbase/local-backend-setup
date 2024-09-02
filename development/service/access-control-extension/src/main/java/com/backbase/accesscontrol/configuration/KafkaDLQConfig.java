package com.backbase.accesscontrol.configuration;

import com.backbase.accesscontrol.constant.KafkaConstants;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Configuration
public class KafkaDLQConfig {

    @Bean
    public DeadLetterPublishingRecoverer customDeadLetterPublishingRecoverer(
        KafkaTemplate<Object, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, exception) -> {
                // Determine the DLQ topic based on the original topic
                String originalTopic = record.topic();
                String dlqTopic = switch (originalTopic) {
                    //TODO change it later.
                    case "rch-30730-kep-data-group" -> "rch-30730-kep-data-group-dlq";
                    case "rch-30730-kep-legal-entity" -> "rch-30730-kep-legal-entity-dlq";
                    default -> "default-dlq";
                };

                // Add custom headers
                record.headers().add(KafkaConstants.ERROR_CODE_HEADER, exception.getClass().getSimpleName().getBytes());
                record.headers().add(KafkaConstants.ERROR_MESSAGE_HEADER, exception.getMessage().getBytes());
                record.headers().add(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(exception).getBytes());

                // Route to the appropriate DLQ topic
                return new TopicPartition(dlqTopic, record.partition());
            }
        );
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}