package com.backbase.accesscontrol.controller;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteRecordsOptions;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
@Slf4j
public class TopicResumeController {

    @Value("${kafka.error.check.poll.duration:5000}")
    private long pollDuration;
    private final AdminClient adminClient;

    @PostMapping("/service-api/v3/clear-topic/{topicName}")
    public void clearTopic(@PathVariable("topicName") String topicName,
                           @RequestParam("partition") Integer partitionId) {
        log.debug("Clearing records for topic: {}", topicName);

        // Specify the partition from which you want to delete records
        TopicPartition topicPartition = new TopicPartition(topicName, partitionId);

        // Create a map with the topic partition and the deletion strategy
        Map<TopicPartition, RecordsToDelete> deleteMap = new HashMap<>();

        //Delete all records
        deleteMap.put(topicPartition, RecordsToDelete.beforeOffset(-1L));

        // Use the AdminClient to delete records
        adminClient.deleteRecords(deleteMap);

        log.info("Records cleared successfully for topic: {}", topicName);
    }
}