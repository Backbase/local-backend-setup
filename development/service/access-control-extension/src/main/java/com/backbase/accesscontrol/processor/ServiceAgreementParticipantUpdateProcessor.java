package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.serviceagreement.ParticipantPutBodyDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementItemDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementItemPutDto;
import com.backbase.accesscontrol.domain.service.facades.ServiceAgreementServiceFacade;
import com.backbase.accesscontrol.mapper.PutServiceAgreementEventMapper;
import com.backbase.accesscontrol.mapper.PutServiceAgreementParticipantEventMapper;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.ParticipantsPut;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceAgreementParticipantUpsertProcessor {

    private final ServiceAgreementServiceFacade serviceAgreementServiceFacade;
    private final PutServiceAgreementParticipantEventMapper mapper;

    public ParticipantsPut process(ParticipantsPut requestPayload) {
        List<ParticipantPutBodyDto> updateDto =
            mapper.mapToServiceAgreementParticipantUpdateDto(requestPayload.getParticipants());
        log.debug("Updating Service Agreement Participant: {}", updateDto);
        serviceAgreementServiceFacade.updateParticipants(updateDto);

        log.info("Upsert Service Agreement Participant processed successfully");

        return requestPayload;
    }

}
