package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertDataGroup")
@AllArgsConstructor
public class DataGroupUpsertHandler implements Function<Message<String>, IntegrationDataGroupItemBatchPutRequestBody> {
    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;
    private final ObjectMapper objectMapper;
    private final ConsumerManager consumerManager;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            dataGroupUpsertProcessor.process(requestPayload);

            // Manually acknowledge the message by committing the offset
            acknowledgeMessage(message);

            // Resume the consumer if it was paused previously
            consumerManager.resumeConsumer(message);

            return requestPayload;
        } catch (PayloadParsingException e) {
            // Handle PayloadParsingException by sending the message to the DLQ
            log.error("Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e; // Let Kafka move it to the DLQ
        } catch (Exception e) {
            // Handle all other exceptions as temporary errors
            log.error("Temporary error occurred, Kafka will retry. Error: {}", e.getMessage());

            consumerManager.pauseConsumer(message);
            // Do not acknowledge the message; Kafka will retry it on the next poll
        }

        return null; // Return null if processing failed, leaving the message unacknowledged
    }

    private void acknowledgeMessage(Message<?> message) {
        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
            log.info("Message acknowledged: {}", message);
        } else {
            log.warn("Acknowledgment is null, message not acknowledged: {}", message);
        }
    }

    private IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload. Payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }
}

