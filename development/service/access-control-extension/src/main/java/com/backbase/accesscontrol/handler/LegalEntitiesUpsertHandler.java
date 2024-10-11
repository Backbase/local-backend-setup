package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.LegalEntitiesUpsertProcessor;
import com.backbase.integration.legalentity.rest.spec.v3.Legalentityitem;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertLegalEntity")
@AllArgsConstructor
public class LegalEntitiesUpsertHandler implements Function<Message<String>, Legalentityitem> {

    private final LegalEntitiesUpsertProcessor legalEntitiesUpsertProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public Legalentityitem apply(Message<String> message) {
        log.info("Upsert Legal Entity Message received: {}", message);

        try {
            Legalentityitem requestPayload = parsePayload(message.getPayload());

            return legalEntitiesUpsertProcessor.process(requestPayload);
        } catch (PayloadParsingException e) {
            log.error("Non-retryable exception: Payload parsing error occurred, message will be moved to DLQ: {}",
                message, e);
            throw e;
        } catch (Exception e) {
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }
    }

    private Legalentityitem parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, Legalentityitem.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}