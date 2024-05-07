package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationItemIdentifierDto;
import com.backbase.accesscontrol.domain.dto.PresentationServiceAgreementWithDataGroupsDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.util.strategy.ContactItemStrategy;
import com.backbase.accesscontrol.util.strategy.DataItemExternalIdConverter;
import com.backbase.dbs.accesscontrol.api.client.v3.ServiceAgreementsApi;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataGroupUpsertProcessor {

    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper mapper;
    private final ServiceAgreementsApi serviceAgreementsApi;
    private final DataItemExternalIdConverter dataItemExternalIdConverter;

    public IntegrationDataGroupItemBatchPutRequestBody process(
        IntegrationDataGroupItemBatchPutRequestBody requestPayload) {
        var searchDataGroupRequest = mapper.mapToPresentationSearchDataGroupsRequestDto(requestPayload);
        var searchResult = dataGroupServiceFacade.searchDataGroups(searchDataGroupRequest,
            requestPayload.getType());

        if (isDataGroupExist(searchResult, requestPayload.getName())) {
            log.info("Data group found: {}", searchResult.get(0).getDataGroups().get(0).getName());
            PresentationDataGroupUpdateDto updateDto = mapper.mapToDataGroupUpdateDto(requestPayload);
            convertDataItemExternalIdsToInternal(requestPayload, updateDto);
            log.info("Updating Data Group: {}", updateDto);
            dataGroupServiceFacade.updateDataGroup(updateDto);
        } else {
            log.info("Data group not found: {}", searchResult.get(0).getDataGroups().get(0).getName());
            DataGroupBaseDto createDto = mapper.mapToDataGroupBaseDto(requestPayload);
            convertDataItemExternalIdsToInternal(requestPayload, createDto);
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

    private void convertDataItemExternalIdsToInternal(IntegrationDataGroupItemBatchPutRequestBody requestBody,
        PresentationDataGroupUpdateDto dto) {
        if (containsExternalItemIdentifier(requestBody.getDataItems())) {

            String serviceAgreementInternalId = ContactItemStrategy.DATA_ITEM_TYPE.equals(requestBody.getType())
                ? serviceAgreementsApi.getServiceAgreementExternalId(
                    requestBody.getDataGroupIdentifier().getNameIdentifier().getExternalServiceAgreementId())
                .getId()
                : null;
            List<String> internalIds = dataItemExternalIdConverter
                .convertDataItemExternalIdsToInternal(requestBody.getDataItems(),
                    requestBody.getType(), serviceAgreementInternalId);
            dto.setDataItems(internalIds.stream().map(id -> {
                    var itemDto = new PresentationItemIdentifierDto();
                    itemDto.setInternalIdIdentifier(id);
                    return itemDto;
                })
                .toList());
        }
    }

    private boolean containsExternalItemIdentifier(List<IntegrationItemIdentifier> list) {
        return list != null && list.stream()
            .anyMatch(dataItem -> dataItem != null && dataItem.getExternalIdIdentifier() != null);
    }

    private void convertDataItemExternalIdsToInternal(IntegrationDataGroupItemBatchPutRequestBody requestBody,
        DataGroupBaseDto dto) {
        if (containsExternalItemIdentifier(requestBody.getDataItems())) {

            String serviceAgreementInternalId = ContactItemStrategy.DATA_ITEM_TYPE.equals(requestBody.getType())
                ? serviceAgreementsApi.getServiceAgreementExternalId(
                    requestBody.getDataGroupIdentifier().getNameIdentifier().getExternalServiceAgreementId())
                .getId()
                : null;
            List<String> internalIds = dataItemExternalIdConverter
                .convertDataItemExternalIdsToInternal(requestBody.getDataItems(),
                    requestBody.getType(), serviceAgreementInternalId);
            dto.setItems(internalIds);
        }
    }


}
