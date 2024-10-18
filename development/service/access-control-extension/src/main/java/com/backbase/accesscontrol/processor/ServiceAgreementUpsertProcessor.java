package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.datagroup.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.datagroup.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.datagroup.PresentationServiceAgreementWithDataGroupsDto;
import com.backbase.accesscontrol.domain.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementItemDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementItemPutDto;
import com.backbase.accesscontrol.domain.service.facades.DataGroupServiceFacade;
import com.backbase.accesscontrol.domain.service.facades.ServiceAgreementServiceFacade;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.mapper.PutServiceAgreementEventMapper;
import com.backbase.accesscontrol.persistence.enums.CustomerCategory;
import com.backbase.accesscontrol.persistence.enums.LegalEntityType;
import com.backbase.accesscontrol.util.DataItemsUtil;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceAgreementUpsertProcessor {

    private final ServiceAgreementServiceFacade serviceAgreementServiceFacade;
    private final PutServiceAgreementEventMapper mapper;

    public ServiceAgreement process(ServiceAgreement requestPayload) {
        try {
            ServiceAgreementItemDto searchResult = serviceAgreementServiceFacade.getServiceAgreementByExternalId(requestPayload.getExternalId());
            log.debug("Service Agreement found: {}", searchResult.getName());
            ServiceAgreementItemPutDto updateDto = mapper.mapToServiceAgreementUpdateDto(requestPayload);
            log.debug("Updating Service Agreement: {}", updateDto);
            serviceAgreementServiceFacade.updateServiceAgreement(updateDto, searchResult.getId());
        } catch (NotFoundException exception) {
            log.debug("Service agreement not found: {}", requestPayload.getName());
            ServiceAgreementDto createDto = mapper.mapToServiceAgreementCreateDto(requestPayload);
            log.debug("Creating Service Agreement: {}", createDto);
            serviceAgreementServiceFacade.ingestServiceAgreement(createDto);
        }

        log.info("Upsert Service Agreement processed successfully");

        return requestPayload;
    }

}
