package com.backbase.accesscontrol.cloudfunction;

import com.backbase.accesscontrol.domain.dto.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationServiceAgreementWithDataGroupsDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.util.DataItemsUtil;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("upsertDataGroup")
@AllArgsConstructor
@Slf4j
public class UpsertDataGroupCloudFunction implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, IntegrationDataGroupItemBatchPutRequestBody> {

    private final DataItemsUtil dataItemsUtil;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper mapper;

    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Upsert Data Message received: {}", message);
        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

        IntegrationDataGroupItemBatchPutRequestBody requestPayload = message.getPayload();
        var searchDataGroupRequest = mapper.mapToPresentationSearchDataGroupsRequestDto(requestPayload);
        var searchResult = dataGroupServiceFacade.searchDataGroups(searchDataGroupRequest, requestPayload.getType());

        if (isDataGroupExist(searchResult, requestPayload.getName())) {
            log.info("Data group found: {}", searchResult.get(0).getDataGroups().get(0).getName());
            PresentationDataGroupUpdateDto updateDto = mapper.mapToDataGroupUpdateDto(requestPayload);
            dataItemsUtil.updateDataItems(requestPayload.getDataItems(), updateDto);
            log.info("Updating Data Group: {}", updateDto);
            dataGroupServiceFacade.updateDataGroup(updateDto);
        } else {
            log.info("Data group not found: {}", searchResult.get(0).getDataGroups().get(0).getName());
            DataGroupBaseDto createDto = mapper.mapToDataGroupBaseDto(requestPayload);
            dataItemsUtil.updateDataItems(requestPayload.getDataItems(), createDto);
            log.info("Creating Data Group: {}", createDto);
            dataGroupServiceFacade.createDataGroup(createDto);
        }

        log.info("Upsert Data Event processed successfully");

        if (acknowledgment != null) {
            log.info("Acknowledgment provided");
            acknowledgment.acknowledge();
        }

        return requestPayload;
    }

    private boolean isDataGroupExist(List<PresentationServiceAgreementWithDataGroupsDto> searchResult, String name) {

        return searchResult.stream()
            .map(PresentationServiceAgreementWithDataGroupsDto::getDataGroups)
            .flatMap(List::stream)
            .anyMatch(dataGroup -> dataGroup.getName().equals(name));
    }

}
