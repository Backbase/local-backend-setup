package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.domain.service.facades.LegalEntityServiceFacade;
import com.backbase.accesscontrol.mapper.PutLegalEntityEventMapper;
import com.backbase.accesscontrol.persistence.enums.CustomerCategory;
import com.backbase.accesscontrol.persistence.enums.LegalEntityType;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.legalentity.rest.spec.v3.Legalentityitem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegalEntitiesUpsertProcessor {

    private final LegalEntityServiceFacade legalEntityServiceFacade;
    private final PutLegalEntityEventMapper mapper;

    public Legalentityitem process(Legalentityitem requestPayload) {
        try {
            LegalEntityType newLegalEntityType =
                mapper.toLegalEntityDomainType(requestPayload.getLegalEntity().getType());
            CustomerCategory newCustomerCategory =
                mapper.toCustomerCategoryDomainType(requestPayload.getLegalEntity().getCustomerCategory());
            log.debug("Updating Legal Entity: {}", requestPayload);
            legalEntityServiceFacade.updateLegalEntityByExternalId(newLegalEntityType, newCustomerCategory,
                requestPayload.getExternalId());
        } catch (NotFoundException exception) {
            log.debug("Legal Entity not found: {}", requestPayload.getExternalId());
            CreateLegalEntityRequest createDto = mapper.mapToCreateLegalEntity(requestPayload.getLegalEntity());
            log.debug("Creating Legal Entity: {}", createDto);
            legalEntityServiceFacade.createLegalEntity(createDto);
        }
        return requestPayload;
    }

}
