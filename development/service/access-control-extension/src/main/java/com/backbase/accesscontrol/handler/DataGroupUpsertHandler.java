package com.backbase.accesscontrol.handler;

import java.util.function.Function;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("upsertDataGroup")
@AllArgsConstructor
public class DataGroupUpsertHandler implements Function<Message<String>, IntegrationDataGroupItemBatchPutRequestBody> {

    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(Message<String> message) {
        log.info("Upsert Data Group Message received: {}", message);

        try {
            IntegrationDataGroupItemBatchPutRequestBody requestPayload = parsePayload(message.getPayload());

            return dataGroupUpsertProcessor.process(requestPayload);
        } catch (PayloadParsingException | NotFoundException | BadRequestException e) {
            log.error("Non-retryable exception: Message will be moved to DLQ: {}", message, e);
            throw e;
        } catch (Exception e) {
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }
    }

    private IntegrationDataGroupItemBatchPutRequestBody parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, IntegrationDataGroupItemBatchPutRequestBody.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
