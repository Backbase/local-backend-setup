package com.backbase.accesscontrol.cloudfunction;

import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaErrorTopicChecker {

    private final ConsumerFactory<String, String> consumerFactory;

    // Method to check if there are messages on the error topic
    public boolean areMessagesOnErrorTopic(String topicName, Integer partition) {
        try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
            consumer.assign(Collections.singletonList(
                new TopicPartition(topicName, partition))); // Replace with your error topic name
            log.info("Pooling Message from topic {} and partition {}", topicName, partition);
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // Adjust timeout as needed
            return !records.isEmpty(); // Returns true if there are messages, false otherwise
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            return false; // Return false in case of error
        }
    }
}
