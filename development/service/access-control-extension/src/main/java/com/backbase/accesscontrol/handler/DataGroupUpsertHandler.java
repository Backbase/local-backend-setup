package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
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
    private final ConsumerManager consumerManager;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            // Process the message and resume the consumer if successful
            asyncRetryService.retryAsync(() -> {
                try {
                    dataGroupUpsertProcessor.process(requestPayload);
                    acknowledgeMessage(message);

                    // Resume consumer if paused
                    consumerManager.resumeConsumer(message);
                } catch (Exception e) {
                    log.error("Error during retry processing, consumer will remain paused", e);
                    throw e;  // Keep retrying if it fails
                }
            });

            return null; // Asynchronous processing, so return null here
        } catch (PayloadParsingException e) {
            // Handle ParsingPayloadException by sending the message to the DLQ
            log.error("Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e; // Let Kafka move it to the DLQ
        } catch (Exception e) {
            // Pause consumer only for temporary errors (any exception except ParsingPayloadException)
            log.error("Temporary error occurred, consumer will be paused and message retried asynchronously. Error: {}", e.getMessage());
            consumerManager.pauseConsumer(message);

            asyncRetryService.retryAsync(() -> processMessageWithRetry(message));

            // Don't acknowledge, as the message will be retried
        }

        return null; // Return null since the processing is handled asynchronously
    }

    private void processMessageWithRetry(Message<String> message) {
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());
            dataGroupUpsertProcessor.process(requestPayload);
            acknowledgeMessage(message);

            // Resume consumer after successful processing
            consumerManager.resumeConsumer(message);
        } catch (Exception e) {
            log.error("Retry failed for message: {}", message, e);
            throw e; // Rethrow to keep retrying asynchronously
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

