package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
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

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);

        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            // Process the parsed payload
            dataGroupUpsertProcessor.process(requestPayload);

            // Commit offset only after successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

        } catch (PayloadParsingException e) {
            // Non-retryable exception, directly send to DLQ using the error handler
            log.error("Non-retryable exception: Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e;
        } catch (Exception e) {
            // Retryable exception
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }

        return null;
    }

    private IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
