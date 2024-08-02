package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
import com.backbase.accesscontrol.processor.LegalEntitiesUpsertProcessor;
import com.backbase.accesscontrol.service.ConsumerControlService;
import com.backbase.accesscontrol.service.ErrorHandlingService;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;
import com.fasterxml.jackson.core.type.TypeReference;
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
@Component("upsertLegalEntity")
@AllArgsConstructor

public class LegalEntitiesUpsertHandler implements Function<Message<String>, LegalEntityCreateItem> {
    private final LegalEntitiesUpsertProcessor legalEntitiesUpsertProcessor;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    private final ConsumerManager consumerManager;

    private final ErrorHandlingService errorHandlingService;


    @Override
    public LegalEntityCreateItem apply(Message<String> message) {
        log.info("Upsert Legal Entity Message received: {}", message);
        try {
            LegalEntityCreateItem requestPayload = parsePayload(message.getPayload());

            RetryCallback<LegalEntityCreateItem, RuntimeException> retryCallback = context -> {
                try {
                    // Pause the consumer if retry attempts have started (1)
                    if (context.getRetryCount() == KAFKA_RETRY_PAUSE_COUNT) {
                        consumerManager.pauseConsumer(message);
                    }

                    // Attempt to process the message
                    legalEntitiesUpsertProcessor.process(requestPayload);

                    // Resume the consumer after successful processing if it was previously paused
                    if (context.getRetryCount() > 0 && consumerManager.isConsumerPaused()) {
                        consumerManager.resumeConsumer(message);
                    }

                    return requestPayload; // Return the payload to signal success
                } catch (Exception e) {
                    log.error("Error during processing, will retry. Error: {}", e.getMessage());
                    throw e; // Trigger a retry by rethrowing the exception
                }
            };

            RecoveryCallback<LegalEntityCreateItem> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message, context.getLastThrowable());

                // Consumer is resumed after retries are exhausted if it was paused
                if (consumerManager.isConsumerPaused()) {
                    consumerManager.resumeConsumer(message);
                }
                errorHandlingService.handleFailure(message, (Exception) context.getLastThrowable());
                return null;
            };

            return retryTemplate.execute(retryCallback, recoveryCallback);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing message: {}", message, e);
            errorHandlingService.handleFailure(message, e);
        }

        return null;
    }


    private LegalEntityCreateItem parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error parsing message payload. Payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

}