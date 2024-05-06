package com.backbase.accesscontrol.handler;

import com.backbase.accesscontrol.processor.DataGroupUpsertProcessor;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Upsert Data Message received: {}", message);

        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();
        return dataGroupUpsertProcessor.process(requestPayload);
    }
}