package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("upsertDataGroup")
@Slf4j
public class DataGroupUpsertHandler implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, IntegrationDataGroupItemBatchPutRequestBody> {

    private final DataGroupUpsertProcessor dataGroupUpsertProcessor;

    public DataGroupUpsertHandler(DataGroupUpsertProcessor dataGroupUpsertProcessor) {
        this.dataGroupUpsertProcessor = dataGroupUpsertProcessor;
    }

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.warn("Request: Partition Id {} and Upsert Data name {}", message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION),
            message.getPayload().getName());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();
        return dataGroupUpsertProcessor.process(requestPayload);
    }
}