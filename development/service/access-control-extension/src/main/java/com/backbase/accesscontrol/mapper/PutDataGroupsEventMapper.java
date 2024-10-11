package com.backbase.accesscontrol.mapper;

import org.mapstruct.Mapping;

import com.backbase.accesscontrol.domain.dto.datagroup.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.datagroup.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.datagroup.PresentationSearchDataGroupsRequestDto;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutDataGroupsEventMapper {

    @Mapping(target = "dataItems", ignore = true)
    PresentationDataGroupUpdateDto map(IntegrationDataGroupItemBatchPutRequestBody requestPayload);

    @Mapping(target = "dataItems", ignore = true)
    PresentationDataGroupUpdateDto mapToDataGroupUpdateDto(IntegrationDataGroupItemBatchPutRequestBody event);

    @Mapping(target = "externalServiceAgreementId",  source = "dataGroupIdentifier.nameIdentifier.externalServiceAgreementId")
    @Mapping(target = "serviceAgreementId", ignore = true)
    @Mapping(target = "items", ignore = true)
    DataGroupBaseDto mapToDataGroupBaseDto(IntegrationDataGroupItemBatchPutRequestBody event);

    @Mapping(target = "serviceAgreementIdentifier.externalIdIdentifier",
        source = "dataGroupIdentifier.nameIdentifier.externalServiceAgreementId")
    @Mapping(target = "serviceAgreementIdentifier.idIdentifier", ignore = true)
    @Mapping(target = "serviceAgreementIdentifier.nameIdentifier", ignore = true)
    @Mapping(target = "dataItemIdentifier", ignore = true)
    @Mapping(target = "legalEntityIdentifier", ignore = true)
    PresentationSearchDataGroupsRequestDto mapToPresentationSearchDataGroupsRequestDto(
        IntegrationDataGroupItemBatchPutRequestBody requestPayload);


}
