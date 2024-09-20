package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.LegalEntitiesUpsertProcessor;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertLegalEntity")
@AllArgsConstructor
public class LegalEntitiesUpsertHandler implements Function<Message<String>, LegalEntityCreateItem> {

    private final LegalEntitiesUpsertProcessor legalEntitiesUpsertProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public LegalEntityCreateItem apply(Message<String> message) {
        log.info("Upsert Legal Entity Message received: {}", message);

        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

        try {
            // Parse the message payload
            LegalEntityCreateItem requestPayload = parsePayload(message.getPayload());

            // Process the parsed payload
            legalEntitiesUpsertProcessor.process(requestPayload);

            // Commit offset only after successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.info("Message acknowledged successfully.");
            }

            return requestPayload;

        } catch (PayloadParsingException e) {
            // Non-retryable exception, directly send to DLQ using the error handler
            log.error("Non-retryable exception: Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e;

        } catch (Exception e) {
            // Retryable exception
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }
    }

    private LegalEntityCreateItem parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, LegalEntityCreateItem.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}