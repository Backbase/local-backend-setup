package com.backbase.accesscontrol.service;

import com.backbase.accesscontrol.configuration.RchKafkaGenericProperties;
import com.backbase.accesscontrol.constant.KafkaConstants;
import com.backbase.accesscontrol.exception.PayloadParsingException;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ErrorHandlingService {

    private final StreamBridge streamBridge;
    private final RchKafkaGenericProperties rchKafkaGenericProperties;

    public void handleFailure(Message<String> message, Exception e) {
        if (e instanceof PayloadParsingException) {
            sendMessageToErrorTopic(message, e);
        }
    }

    private void sendMessageToErrorTopic(Message<String> originalMessage, Exception e) {
        Message<String> errorMessage = MessageBuilder.withPayload(originalMessage.getPayload())
            .copyHeaders(originalMessage.getHeaders())
            .setHeader(KafkaConstants.ERROR_CODE_HEADER, e.getClass().getSimpleName())
            .setHeader(KafkaConstants.ERROR_MESSAGE_HEADER, e.getMessage())
            .setHeader(KafkaConstants.ERROR_STACKTRACE_HEADER, getStackTrace(e))
            .build();

        streamBridge.send(rchKafkaGenericProperties.getUpsertLegalEntitiesErrorTopicName(), errorMessage);
        log.info("Message sent to error topic");
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
