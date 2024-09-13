package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            // Process the parsed payload
            dataGroupUpsertProcessor.process(requestPayload);

            // Resume consumer after successful processing
            consumerManager.resumeConsumer(message);

        } catch (PayloadParsingException e) {
            // Non-retryable exception, directly send to DLQ using the error handler (configured in the customizer)
            log.error("Non-retryable exception: Payload parsing error occurred, message will be moved to DLQ: {}",
                message, e);
            throw e;

        } catch (Exception e) {
            // Retryable exception
            log.error("Temporary error occurred, pausing consumer for retry. Kafka will retry: {}", e.getMessage());

            // Pause consumer for retryable exceptions
            consumerManager.pauseAndResumeAfterDelay(message, 2);

            throw e;
        }

        return null;
    }

    private IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
