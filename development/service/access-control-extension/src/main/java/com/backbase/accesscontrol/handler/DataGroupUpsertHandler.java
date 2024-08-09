package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.accesscontrol.configuration.RchKafkaGenericProperties;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.accesscontrol.service.ErrorHandlingService;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;
    private final ErrorHandlingService errorHandlingService;
    private final ConsumerManager consumerManager;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);
        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            RetryCallback<IntegrationDataGroupItemBatchPutRequestBody, RuntimeException> retryCallback = context -> {
                try {
                    // Pause the consumer if retry attempts have started (1)
                    if (context.getRetryCount() == KAFKA_RETRY_PAUSE_COUNT) {
                        consumerManager.pauseConsumer(message);
                    }

                    // Attempt to process the message
                    dataGroupUpsertProcessor.process(requestPayload);

                    // Resume the consumer after successful processing if it was previously paused
                    if (consumerManager.isConsumerPaused()) {
                        consumerManager.resumeConsumer(message);
                    }

                    return requestPayload; // Return the payload to signal success
                } catch (Exception e) {
                    log.error("Error during processing, will retry. Error: {}", e.getMessage());
                    throw e; // Trigger a retry by rethrowing the exception
                }
            };

            RecoveryCallback<IntegrationDataGroupItemBatchPutRequestBody> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message, context.getLastThrowable());

                // Consumer is resumed after retries are exhausted if it was paused
                if (consumerManager.isConsumerPaused()) {
                    consumerManager.resumeConsumer(message);
                }
                errorHandlingService.handleFailure(message,
                    rchKafkaGenericProperties.getUpsertDataGroupErrorTopicName(),
                    (Exception) context.getLastThrowable());
                return null;
            };

            return retryTemplate.execute(retryCallback, recoveryCallback);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing message: {}", message, e);
            errorHandlingService.handleFailure(message, rchKafkaGenericProperties.getUpsertDataGroupErrorTopicName(),
                e);
        }

        return null;
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