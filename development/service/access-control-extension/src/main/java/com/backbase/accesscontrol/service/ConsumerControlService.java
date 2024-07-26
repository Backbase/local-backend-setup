package com.backbase.accesscontrol.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Collections;

@Slf4j
@Service
public class ConsumerControlService {

    public void controlConsumer(Message<?> message, boolean pause) {
        Consumer<?, ?> consumer = message.getHeaders().get(KafkaHeaders.CONSUMER, Consumer.class);
        String topic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
        Integer partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);

        TopicPartition topicPartition = new TopicPartition(topic, partitionId);
        if (pause) {
            log.info("Pausing the consumer for topic {} and partition {}", topic, partitionId);
            consumer.pause(Collections.singleton(topicPartition));
        } else {
            log.info("Resuming the consumer for topic {} and partition {}", topic, partitionId);
            consumer.resume(Collections.singleton(topicPartition));
        }

    }
}
