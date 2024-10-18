package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.accesscontrol.processor.ServiceAgreementUpsertProcessor;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertDataGroup")
@AllArgsConstructor
public class ServiceAgreementUpsertHandler implements Function<Message<String>, ServiceAgreement> {

    private final ServiceAgreementUpsertProcessor serviceAgreementUpsertProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public ServiceAgreement apply(Message<String> message) {
        log.info("Upsert Service Agreement Message received: {}", message);

        try {
            ServiceAgreement requestPayload = parsePayload(message.getPayload());

            return serviceAgreementUpsertProcessor.process(requestPayload);
        } catch (PayloadParsingException | NotFoundException | BadRequestException e) {
            log.error("Non-retryable exception: Message will be moved to DLQ: {}", message, e);
            throw e;
        } catch (Exception e) {
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }
    }

    private ServiceAgreement parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, ServiceAgreement.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
