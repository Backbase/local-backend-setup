package com.backbase.accesscontrol.handler;

import static com.backbase.accesscontrol.constant.KafkaConstants.KAFKA_RETRY_PAUSE_COUNT;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.ServiceAgreementProcessor;
import com.backbase.accesscontrol.service.ConsumerControlService;
import com.backbase.accesscontrol.service.ErrorHandlingService;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
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
@Component("upsertServiceAgreement")
@AllArgsConstructor
public class ServiceAgreementUpsertHandler implements Function<Message<String>, ServiceAgreement> {
    private final ServiceAgreementProcessor serviceAgreementProcessor;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;
    private final ConsumerControlService consumerControlService;
    private final ErrorHandlingService errorHandlingService;

    @Override
    public ServiceAgreement apply(Message<String> message) {
        log.info("Create Service Agreement Message received: {}", message);
        try {
            ServiceAgreement requestPayload = parsePayload(message.getPayload());

            RetryCallback<ServiceAgreement, RuntimeException> retryCallback = context -> {
                if (context.getRetryCount() > KAFKA_RETRY_PAUSE_COUNT) {
                    consumerControlService.controlConsumer(message, true);
                }
                return serviceAgreementProcessor.process(requestPayload);
            };

            RecoveryCallback<ServiceAgreement> recoveryCallback = context -> {
                log.error("Retries exhausted for message: {}", message, context.getLastThrowable());
                errorHandlingService.handleFailure(message, (Exception) context.getLastThrowable());
                return null;
            };

            ServiceAgreement result = retryTemplate.execute(retryCallback, recoveryCallback);

            consumerControlService.controlConsumer(message, false);

            return result;
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing message: {}", message, e);
            errorHandlingService.handleFailure(message, e);
        }

        return null;
    }

    public ServiceAgreement parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, ServiceAgreement.class);
        } catch (Exception e) {
            log.error("Error parsing message payload", e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

}