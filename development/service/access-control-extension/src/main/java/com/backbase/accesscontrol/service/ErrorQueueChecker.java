package com.backbase.accesscontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
public class ErrorQueueChecker {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    public boolean isErrorQueueEmpty(String errorQueueTopic) {
//        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfig())) {
//            ListConsumerGroupOffsetsResult consumerGroupOffsets = adminClient.listConsumerGroupOffsets(
//                "error-consumer-group");
//            Map<TopicPartition, OffsetAndMetadata> offsets = consumerGroupOffsets.partitionsToOffsetAndMetadata().get();
//
//            // Loop through the partitions of the error queue topic
//            for (TopicPartition partition : offsets.keySet()) {
//                // Assuming error queue topic has only one partition
//                if (partition.topic().equals(errorQueueTopic)) {
//                    // If the offset for the partition is 0, it means the queue is empty
//                    return offsets.get(partition).offset() == 0;
//                }
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            // Handle exception as needed
//        }
        return false; // Return false by default if error occurs
    }

}
