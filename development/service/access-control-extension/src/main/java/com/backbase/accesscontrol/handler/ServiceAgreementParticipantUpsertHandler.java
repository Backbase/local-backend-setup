package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.ServiceAgreementParticipantUpdateProcessor;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.ParticipantsPut;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertServiceAgreementParticipant")
@AllArgsConstructor
public class ServiceAgreementParticipantUpsertHandler implements Function<Message<String>, ParticipantsPut> {

    private final ServiceAgreementParticipantUpdateProcessor serviceAgreementParticipantUpdateProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public ParticipantsPut apply(Message<String> message) {
        log.info("Upsert Service Agreement Participant Message received: {}", message);

        try {
            ParticipantsPut requestPayload = parsePayload(message.getPayload());

            return serviceAgreementParticipantUpdateProcessor.process(requestPayload);
        } catch (PayloadParsingException | NotFoundException | BadRequestException e) {
            log.error("Non-retryable exception: Message will be moved to DLQ: {}", message, e);
            throw e;
        } catch (Exception e) {
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }
    }

    private ParticipantsPut parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, ParticipantsPut.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
