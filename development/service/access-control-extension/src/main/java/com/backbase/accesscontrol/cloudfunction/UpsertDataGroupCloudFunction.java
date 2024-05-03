package com.backbase.accesscontrol.cloudfunction;

import com.backbase.accesscontrol.processor.UpsertDataGroupProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("upsertDataGroup")
@AllArgsConstructor
@Slf4j
public class UpsertDataGroupCloudFunction implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, IntegrationDataGroupItemBatchPutRequestBody> {

    private final UpsertDataGroupProcessor upsertDataGroupProcessor;
    private final StreamBridge streamBridge;
    private final KafkaErrorTopicChecker kafkaErrorTopicChecker;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Upsert Data Message received: {}", message);
        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();
        try {
            return upsertDataGroupProcessor.process(requestPayload);
        } catch (Exception e) {
            log.error("Error processing message. Sending to error queue.", e);
            var consumer = message.getHeaders()
                .get(KafkaHeaders.CONSUMER, org.apache.kafka.clients.consumer.Consumer.class);
            var topic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
            var partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);
            var errorTopic = "UpsertDataGroupsFailureEvent";

            sendMessageToErrorTopic(errorTopic, e, requestPayload, partitionId);

            log.info("Pausing the consumer for topic {} and partition {}", topic, partitionId);
            consumer.pause(Collections.singleton(new TopicPartition(topic, partitionId)));

            // Loop until there are no messages on the error topic
            while (kafkaErrorTopicChecker.areMessagesOnErrorTopic(errorTopic, partitionId)) {
                try {
                    // Sleep for some time before checking again
                    log.info("Sleeping for 10 seconds");
                    TimeUnit.SECONDS.sleep(10); // Adjust sleep duration as needed
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

            log.info("Resuming the consumer for topic {} and partition {}", topic, partitionId);
            consumer.resume(Collections.singleton(new TopicPartition(topic, partitionId)));
            return null;
        }
    }


    private void sendMessageToErrorTopic(String errorTopic, Exception e,
        IntegrationDataGroupItemBatchPutRequestBody requestPayload,
        Integer partitionId) {
        Message<IntegrationDataGroupItemBatchPutRequestBody> errorMessage =
            MessageBuilder.withPayload(requestPayload)
                .setHeader("error-reason", e)
                .setHeader(KafkaHeaders.PARTITION, partitionId)
                .build();
        streamBridge.send(errorTopic, errorMessage);
        log.info("Message Sent to ErrorTopic");
    }

}
