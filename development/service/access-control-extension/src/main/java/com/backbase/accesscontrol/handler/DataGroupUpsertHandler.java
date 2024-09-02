package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.accesscontrol.service.AsyncRetryService;
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
    private final AsyncRetryService asyncRetryService;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            // Asynchronously process the message with retries
            asyncRetryService.retryAsync(() -> processMessage(requestPayload, message));

            // Return null since the actual processing is handled asynchronously
            return null;
        } catch (PayloadParsingException e) {
            log.error("Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e; // Let Kafka move it to the DLQ
        }
    }

    private void processMessage(IntegrationDataGroupItemBatchPutRequestBody requestPayload, Message<String> message) {
        try {
            dataGroupUpsertProcessor.process(requestPayload);
            acknowledgeMessage(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            throw e; // Trigger retry by rethrowing the exception
        }
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

