package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.accesscontrol.configuration.RchKafkaGenericProperties;
import com.backbase.accesscontrol.constant.KafkaConstants;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.LegalEntitiesUpsertProcessor;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityPut;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertLegalEntity")
@AllArgsConstructor
public class LegalEntitiesUpsertHandler implements Function<Message<String>, List<LegalEntityPut>> {
    private final LegalEntitiesUpsertProcessor legalEntitiesUpsertProcessor;
    private final StreamBridge streamBridge;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<LegalEntityPut> apply(Message<String> message) {
        log.info("Create Legal Entities Message received: {}", message);
        try {
            List<LegalEntityPut> requestPayload = parsePayload(message.getPayload());

            RetryCallback<List<LegalEntityPut>, RuntimeException> retryCallback =
                context -> {
                    if (context.getRetryCount() > KAFKA_RETRY_PAUSE_COUNT) {
                        pauseConsumer(message);
                    }
                    return legalEntitiesUpsertProcessor.process(requestPayload);
                };

            RecoveryCallback<List<LegalEntityPut>> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message);
                Exception finalException = (Exception) context.getLastThrowable();
                handleFailure(message, finalException);

                return Collections.emptyList();
            };

            List<LegalEntityPut> result = retryTemplate.execute(retryCallback, recoveryCallback);
            resumeConsumer(message);
            return result;
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            handleFailure(message, e);
        }

        return Collections.emptyList();
    }

    public List<LegalEntityPut> parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error parsing message payload", e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

    private void handleFailure(Message<String> message, Exception e) {
        if (e instanceof PayloadParsingException) {
            sendMessageToErrorTopic(message, e);
        }
    }

    private void pauseConsumer(Message<String> message) {
        var consumer = message.getHeaders()
            .get(KafkaHeaders.CONSUMER, org.apache.kafka.clients.consumer.Consumer.class);
        var topic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
        var partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);

        log.info("Pausing the consumer for topic {} and partition {}", topic, partitionId);
        consumer.pause(Collections.singleton(new TopicPartition(topic, partitionId)));
    }

    private void resumeConsumer(Message<String> message) {
        var consumer = message.getHeaders()
            .get(KafkaHeaders.CONSUMER, org.apache.kafka.clients.consumer.Consumer.class);
        var topic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
        var partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);

        log.info("Resuming the consumer for topic {} and partition {}", topic, partitionId);
        consumer.resume(Collections.singleton(new TopicPartition(topic, partitionId)));
    }

    private void sendMessageToErrorTopic(Message<String> originalMessage, Exception e) {
        MessageBuilder<String> errorMessageBuilder = MessageBuilder.withPayload(originalMessage.getPayload())
            .copyHeaders(originalMessage.getHeaders())
            .setHeader(KafkaConstants.ERROR_CODE_HEADER, e.getClass().getSimpleName())
            .setHeader(KafkaConstants.ERROR_MESSAGE_HEADER, e.getMessage())
            .setHeader(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(e));

        Message<String> errorMessage = errorMessageBuilder.build();
        streamBridge.send(rchKafkaGenericProperties.getUpsertLegalEntitiesErrorTopicName(), errorMessage);
        log.info("Message Sent to ErrorTopic");
    }


    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}