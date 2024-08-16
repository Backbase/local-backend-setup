package com.backbase.dbs.user.manager.handler;

import static com.backbase.dbs.user.manager.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.dbs.user.manager.ConsumerManager;
import com.backbase.dbs.user.manager.configuration.RchKafkaGenericProperties;
import com.backbase.dbs.user.manager.exception.PayloadParsingException;
import com.backbase.dbs.user.manager.processor.UserUpsertProcessor;
import com.backbase.dbs.user.manager.service.ErrorHandlingService;
import com.backbase.integration.usermanager.rest.spec.v3.UserExternal;
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
@Component("upsertUser")
@AllArgsConstructor

public class UserUpsertHandler implements Function<Message<String>, UserExternal> {
    private final UserUpsertProcessor userUpsertProcessor;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    private final ConsumerManager consumerManager;

    private final ErrorHandlingService errorHandlingService;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;

    @Override
    public UserExternal apply(Message<String> message) {
        log.info("Upsert User Message received: {}", message);
        try {
            UserExternal requestPayload = parsePayload(message.getPayload());

            RetryCallback<UserExternal, RuntimeException> retryCallback = context -> {
                try {
                    // Pause the consumer if retry attempts have started (1)
                    if (context.getRetryCount() == KAFKA_RETRY_PAUSE_COUNT) {
                        consumerManager.pauseConsumer(message);
                    }

                    // Attempt to process the message
                    userUpsertProcessor.process(requestPayload);

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

            RecoveryCallback<UserExternal> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message, context.getLastThrowable());

                // Consumer is resumed after retries are exhausted if it was paused
                if (consumerManager.isConsumerPaused()) {
                    consumerManager.resumeConsumer(message);
                }
                errorHandlingService.handleFailure(message,
                    rchKafkaGenericProperties.getUpsertUserErrorTopicName(),
                    (Exception) context.getLastThrowable());
                return null;
            };

            return retryTemplate.execute(retryCallback, recoveryCallback);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing message: {}", message, e);
            errorHandlingService.handleFailure(message,
                rchKafkaGenericProperties.getUpsertUserErrorTopicName(), e);
        }

        return null;
    }


    private UserExternal parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error parsing message payload. Payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

}