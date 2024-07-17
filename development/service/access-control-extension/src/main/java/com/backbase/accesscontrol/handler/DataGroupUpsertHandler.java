package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.configuration.RchKafkaGenericProperties;
import com.backbase.accesscontrol.constant.KafkaConstants;
import com.backbase.accesscontrol.exception.DataProcessingException;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.kafka.KafkaErrorTopicChecker;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertDataGroup")
@AllArgsConstructor
public class DataGroupUpsertHandler implements Function<Message<String>, IntegrationDataGroupItemBatchPutRequestBody> {
    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;
    private final StreamBridge streamBridge;
    private final KafkaErrorTopicChecker kafkaErrorTopicChecker;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            RetryCallback<IntegrationDataGroupItemBatchPutRequestBody, RuntimeException> retryCallback =
                context -> dataGroupUpsertProcessor.process(requestPayload);

            RecoveryCallback<IntegrationDataGroupItemBatchPutRequestBody> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message);

                Exception finalException = (Exception) context.getLastThrowable();
                throw new DataProcessingException("Couldn't process the message after retries.", finalException);
            };

            return retryTemplate.execute(retryCallback, recoveryCallback);
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

        sendMessageToErrorTopic(message, e);
/*
        log.info("Pausing the consumer for topic {} and partition {}", topic, partitionId);
        consumer.pause(Collections.singleton(new TopicPartition(topic, partitionId)));

        waitForErrorTopicToClear(KAFKA_ERROR_TOPIC, partitionId);

        log.info("Resuming the consumer for topic {} and partition {}", topic, partitionId);
        consumer.resume(Collections.singleton(new TopicPartition(topic, partitionId)));*/
    }

/*    private void waitForErrorTopicToClear(String errorTopic) {
        while (kafkaErrorTopicChecker.areMessagesOnErrorTopic(errorTopic)) {
            try {
                log.info("Sleeping for 10 seconds");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for error topic to clear", ex);
                break;
            }
        }
    }*/

    private void sendMessageToErrorTopic(Message<String> originalMessage, Exception e) {
        MessageBuilder<String> errorMessageBuilder = MessageBuilder.withPayload(originalMessage.getPayload())
            .copyHeaders(originalMessage.getHeaders())
            .setHeader(KafkaConstants.ERROR_CODE_HEADER, e.getClass().getSimpleName())
            .setHeader(KafkaConstants.ERROR_MESSAGE_HEADER, e.getMessage())
            .setHeader(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(e));

        Message<String> errorMessage = errorMessageBuilder.build();
        streamBridge.send(rchKafkaGenericProperties.getUpsertDataGroupErrorTopicName(), errorMessage);
        log.info("Message Sent to ErrorTopic");
    }


    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}