package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.accesscontrol.processor.FunctionGroupUpsertProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("upsertFunctionGroup")
@AllArgsConstructor
public class FunctionGroupUpsertHandler implements Function<Message<String>, FunctionGroupUpsertDTO> {

    private final FunctionGroupUpsertProcessor functionGroupUpsertProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public FunctionGroupUpsertDTO apply(Message<String> message) {
        log.info("Upsert Function Group Message received: {}", message);

        try {
            FunctionGroupUpsertDTO requestPayload = parsePayload(message.getPayload());

            return functionGroupUpsertProcessor.process(requestPayload);
        } catch (PayloadParsingException e) {
            log.error("Non-retryable exception: Payload parsing error occurred, message will be moved to DLQ: {}", message, e);
            throw e;
        } catch (Exception e) {
            log.error("Temporary error occurred, Kafka will retry: {}", e.getMessage());
            throw e;
        }

    }

    private FunctionGroupUpsertDTO parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, FunctionGroupUpsertDTO.class);
        } catch (Exception e) {
            log.error("Error parsing message payload: {}", payload);
            throw new PayloadParsingException("Error parsing message payload", e);  // Non-retryable
        }
    }
}
