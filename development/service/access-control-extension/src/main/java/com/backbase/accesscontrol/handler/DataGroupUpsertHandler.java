package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("upsertDataGroup")
@Slf4j
public class DataGroupUpsertHandler implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, Message<IntegrationDataGroupItemBatchPutRequestBody>> {

    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;

    public DataGroupUpsertHandler(DataGroupUpsertProcessor dataGroupUpsertProcessor) {
        this.dataGroupUpsertProcessor = dataGroupUpsertProcessor;
    }

    @Override
    public Message<IntegrationDataGroupItemBatchPutRequestBody> apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Upsert Data Message received: {}", message);
        var partitionId = message.getHeaders().get("kafka_receivedPartitionId", Integer.class);
        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();

        return MessageBuilder.withPayload(dataGroupUpsertProcessor.process(requestPayload))
            .setHeader(KafkaHeaders.PARTITION_ID, partitionId)
            .build();
    }
}