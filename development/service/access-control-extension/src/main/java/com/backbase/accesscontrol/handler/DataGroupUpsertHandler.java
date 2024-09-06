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
    private final ConsumerManager consumerManager; // Keep for pause/resume control

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            dataGroupUpsertProcessor.process(requestPayload);

            consumerManager.resumeConsumer(message);

            return requestPayload;
        } catch (PayloadParsingException e) {
            // Handle ParsingPayloadException by sending the message to the DLQ
            log.error("Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e; // Let Kafka move it to the DLQ
        } catch (Exception e) {
            // Pause consumer for temporary errors (any exception except ParsingPayloadException)
            log.error("Temporary error occurred, pausing consumer: {}", e.getMessage());
            consumerManager.pauseAndResumeAfterDelay(message, 10000);  // Pause and resume after 10 seconds

            // Do not acknowledge the message, Kafka will retry on next poll
            throw e; // Rethrow exception to let Kafka retry the message
        }
    }

    private IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

}

