package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.domain.service.facades.LegalEntityServiceFacade;
import com.backbase.accesscontrol.mapper.PutLegalEntityEventMapper;
import com.backbase.accesscontrol.persistence.enums.CustomerCategory;
import com.backbase.accesscontrol.persistence.enums.LegalEntityType;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegalEntitiesUpsertProcessor {

    private final LegalEntityServiceFacade legalEntityServiceFacade;
    private final PutLegalEntityEventMapper mapper;

    public void process(LegalEntityCreateItem requestPayload) {
        try {
            var searchResult =
                legalEntityServiceFacade.getLegalEntityByExternalId(requestPayload.getExternalId());
            log.debug("Legal Entity found: {}", searchResult.getName());
            LegalEntityType newLegalEntityType = mapper.toLegalEntityDomainType(requestPayload.getType());
            CustomerCategory newCustomerCategory =
                mapper.toCustomerCategoryDomainType(requestPayload.getCustomerCategory());
            log.debug("Updating Legal Entity: {}", requestPayload);
            legalEntityServiceFacade.updateLegalEntityByExternalId(newLegalEntityType, newCustomerCategory,
                requestPayload.getExternalId());

        } catch (NotFoundException exception) {
            log.debug("Legal Entity not found: {}", requestPayload.getExternalId());
            CreateLegalEntityRequest createDto = mapper.mapToCreateLegalEntity(requestPayload);
            log.debug("Creating Legal Entity: {}", createDto);
            legalEntityServiceFacade.createLegalEntity(createDto);
        }

    }

}
