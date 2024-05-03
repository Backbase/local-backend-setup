package com.backbase.accesscontrol.controller;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TopicResumeController {

    private final ConsumerFactory<String, String> consumerFactory;

    @RequestMapping(
        method = RequestMethod.POST,
        value = "/service-api/v3/resume-topic/{topicName}",
        produces = "application/json"
    )
    public void resumeTopic(@PathVariable("topicName") String topicName,
        @RequestParam("partition") Integer partitionId) {
        log.info("Resuming topic {} and partition {}", topicName, partitionId);
        try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
            TopicPartition topicPartition = new TopicPartition(topicName, partitionId);
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.resume(Collections.singleton(topicPartition));
        } catch (Exception e) {
            log.error("Error occurred while resuming topic and partition", e);
        }
    }
}
