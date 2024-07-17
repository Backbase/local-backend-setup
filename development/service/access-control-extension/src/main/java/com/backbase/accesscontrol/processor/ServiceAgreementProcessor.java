package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.dto.ServiceAgreementDto;
import com.backbase.accesscontrol.dto.ServiceAgreementItemPutDto;
import com.backbase.accesscontrol.mapper.PutServiceAgreementsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.ServiceAgreementServiceFacade;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceAgreementProcessor {

    private final ServiceAgreementServiceFacade serviceAgreementServiceFacade;
    private final PutServiceAgreementsEventMapper mapper;

    public ServiceAgreement process(ServiceAgreement requestPayload) {
        try {
            var searchResult =
                serviceAgreementServiceFacade.getServiceAgreementByExternalId(requestPayload.getExternalId());
            log.debug("Service agreement found: {}", searchResult.getName());
            ServiceAgreementItemPutDto updateDto = mapper.mapToServiceAgreementItemPutDto(requestPayload);
            log.debug("Updating Service Agreement: {}", updateDto);
            serviceAgreementServiceFacade.updateServiceAgreement(updateDto, requestPayload.getExternalId());

        } catch (NotFoundException exception) {
            log.debug("Service agreement not found: {}", requestPayload.getName());
            ServiceAgreementDto createDto = mapper.mapToDataGroupBaseDto(requestPayload);
            log.debug("Creating Service Agreement: {}", createDto);
            serviceAgreementServiceFacade.ingestServiceAgreement(createDto);
        }

        log.warn("Upsert Service Agreement Event processed successfully");
        return requestPayload;
    }
}
