package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_ERROR_TOPIC;
import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_ERROR_TOPIC_HEADER_NAME;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.kafka.KafkaErrorTopicChecker;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertDataGroup")
@AllArgsConstructor
public class DataGroupUpsertHandler implements Function<Message<String>, IntegrationDataGroupItemBatchPutRequestBody> {
    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;
    private final StreamBridge streamBridge;
    private final KafkaErrorTopicChecker kafkaErrorTopicChecker;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Message received: {}", message);

        IntegrationDataGroupItemBatchPutRequestBody requestPayload;
        try {
            requestPayload = parsePayload(message.getPayload());
            IntegrationDataGroupItemBatchPutRequestBody finalRequestPayload = requestPayload;
            return retryTemplate.execute(context -> dataGroupUpsertProcessor.process(finalRequestPayload));
        }
        catch (PayloadParsingException e) {
            handleFailure(message, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            handleFailure(message, e);
        }

        return null;
    }

    public IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload", e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

    private void handleFailure(Message<String> message, Exception e) {
        var consumer = message.getHeaders()
            .get(KafkaHeaders.CONSUMER, org.apache.kafka.clients.consumer.Consumer.class);
        var topic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
        var partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);

        sendMessageToErrorTopic(e, message.getPayload(), partitionId);

        log.info("Pausing the consumer for topic {} and partition {}", topic, partitionId);
        consumer.pause(Collections.singleton(new TopicPartition(topic, partitionId)));

        waitForErrorTopicToClear(KAFKA_ERROR_TOPIC, partitionId);

        log.info("Resuming the consumer for topic {} and partition {}", topic, partitionId);
        consumer.resume(Collections.singleton(new TopicPartition(topic, partitionId)));
    }

    private void waitForErrorTopicToClear(String errorTopic, Integer partitionId) {
        while (kafkaErrorTopicChecker.areMessagesOnErrorTopic(errorTopic, partitionId)) {
            try {
                log.info("Sleeping for 10 seconds");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for error topic to clear", ex);
                break;
            }
        }
    }

    private void sendMessageToErrorTopic(Exception e, Object requestPayload, Integer partitionId) {
        Message<Object> errorMessage =
            MessageBuilder.withPayload(requestPayload)
                .setHeader(KAFKA_ERROR_TOPIC_HEADER_NAME, e)
                .setHeader(KafkaHeaders.PARTITION, partitionId)
                .build();
        streamBridge.send(KAFKA_ERROR_TOPIC, errorMessage);
        log.info("Message Sent to ErrorTopic");
    }
}