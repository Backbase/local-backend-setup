package com.backbase.accesscontrol.cloudfunction;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.util.DataItemsUtil;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("updateDataGroup")
@AllArgsConstructor
@Slf4j
public class UpdateDataGroupCloudFunction implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, IntegrationDataGroupItemBatchPutRequestBody> {

    private final DataItemsUtil dataItemsUtil;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper mapper;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Message received: {}", message);
        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();

        PresentationDataGroupUpdateDto updateDto = mapper.mapToDataGroupUpdateDto(requestPayload);
        dataItemsUtil.updateDataItems(requestPayload.getDataItems(), updateDto);
        dataGroupServiceFacade.updateDataGroup(updateDto);

        log.info("Event processed successfully");

        if (acknowledgment != null) {
            log.info("Acknowledgment provided");
            acknowledgment.acknowledge();
        }

        return requestPayload;
    }


}
