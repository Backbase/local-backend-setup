package com.backbase.accesscontrol.kafka;

import java.time.Duration;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaErrorTopicChecker {

    private final ConsumerFactory<String, String> consumerFactory;
    //2 sec
    @Value("${kafka.error.check.poll.duration:5000}")
    private long pollDuration;

    public boolean areMessagesOnErrorTopic(String errorTopic, int partitionId) {
        try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
            consumer.assign(Collections.singletonList(new TopicPartition(errorTopic, partitionId)));
            consumer.seekToBeginning(Collections.singletonList(new TopicPartition(errorTopic, partitionId)));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollDuration));
            log.error("Error message count on error topic:{}:" , records.count());

            return records.count() > 0;
        } catch (Exception e) {
            log.error("Error checking messages on error topic: " + e.getMessage());
            return false;
        }
    }
}
