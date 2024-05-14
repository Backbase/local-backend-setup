package com.backbase.datagroup.handler;

import com.backbase.datagroup.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("upsertDataGroup")
@Slf4j
public class DataGroupUpsertHandler implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, Message<IntegrationDataGroupItemBatchPutRequestBody>> {

    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;
    private final ListenerContainerCustomizer listenerContainerCustomizer;

    public DataGroupUpsertHandler(DataGroupUpsertProcessor dataGroupUpsertProcessor,
        ListenerContainerCustomizer listenerContainerCustomizer) {
        this.dataGroupUpsertProcessor = dataGroupUpsertProcessor;
        this.listenerContainerCustomizer = listenerContainerCustomizer;
    }

    @Override
    public Message<IntegrationDataGroupItemBatchPutRequestBody> apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Upsert Data Message received: {}", message);
        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();

        return MessageBuilder.withPayload(dataGroupUpsertProcessor.process(requestPayload))
            .setHeader(KafkaHeaders.PARTITION_ID, message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID))
            .setHeader(KafkaHeaders.MESSAGE_KEY, message.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY))
            .build();
    }
}