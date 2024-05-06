package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationServiceAgreementWithDataGroupsDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.util.DataItemsUtil;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataGroupUpsertProcessor {

    private final DataItemsUtil dataItemsUtil;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper mapper;

    public IntegrationDataGroupItemBatchPutRequestBody process(IntegrationDataGroupItemBatchPutRequestBody requestPayload) {
        var searchDataGroupRequest = mapper.mapToPresentationSearchDataGroupsRequestDto(requestPayload);
        var searchResult = dataGroupServiceFacade.searchDataGroups(searchDataGroupRequest,
            requestPayload.getType());

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
        return requestPayload;
    }

    private boolean isDataGroupExist(List<PresentationServiceAgreementWithDataGroupsDto> searchResult, String name) {

        return searchResult.stream()
            .map(PresentationServiceAgreementWithDataGroupsDto::getDataGroups)
            .flatMap(List::stream)
            .anyMatch(dataGroup -> dataGroup.getName().equals(name));
    }

}