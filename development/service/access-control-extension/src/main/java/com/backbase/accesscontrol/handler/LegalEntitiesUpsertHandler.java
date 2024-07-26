package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.accesscontrol.exception.PayloadParsingException;
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

    private final ConsumerControlService consumerControlService;

    private final ErrorHandlingService errorHandlingService;


    @Override
    public LegalEntityCreateItem apply(Message<String> message) {
        log.info("Upsert Legal Entity Message received: {}", message);
        try {
            LegalEntityCreateItem requestPayload = parsePayload(message.getPayload());

            RetryCallback<LegalEntityCreateItem, RuntimeException> retryCallback = context -> {
                if (context.getRetryCount() == KAFKA_RETRY_PAUSE_COUNT) {
                    consumerControlService.controlConsumer(message, true);
                }
                return legalEntitiesUpsertProcessor.process(requestPayload);
            };

            RecoveryCallback<LegalEntityCreateItem> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message, context.getLastThrowable());
                consumerControlService.controlConsumer(message, false);
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