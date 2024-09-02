package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.manager.ConsumerManager;
import com.backbase.accesscontrol.processor.LegalEntitiesUpsertProcessor;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;
import com.fasterxml.jackson.core.type.TypeReference;
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
        try {
            // Parse the message payload
            LegalEntityCreateItem requestPayload = parsePayload(message.getPayload());

            // Attempt to process the message
            legalEntitiesUpsertProcessor.process(requestPayload);

            // Manually acknowledge the message by committing the offset
            acknowledgeMessage(message);

            // Return the successfully processed payload
            return requestPayload;
        } catch (PayloadParsingException e) {
            // Fail fast for payload parsing errors
            log.error("Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e; // Let Kafka move it to the DLQ
        } catch (Exception e) {
            // Handle all other exceptions as temporary errors
            log.error("Temporary error occurred, Kafka will retry. Error: {}", e.getMessage());
            // Do not acknowledge the message; Kafka will retry it on the next poll
        }

        return null; // Return null if processing failed, leaving the message unacknowledged
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

    private LegalEntityCreateItem parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error parsing message payload. Payload: {}", payload, e);
            throw new PayloadParsingException("Error parsing message payload", e);
        }
    }

}