package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.serviceagreement.ParticipantPutBodyDto;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationParticipantPutItem;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PutServiceAgreementParticipantEventMapper {

    List<ParticipantPutBodyDto> mapToServiceAgreementParticipantUpdateDto(List<IntegrationParticipantPutItem> participants);

    ParticipantPutBodyDto map(IntegrationParticipantPutItem participant);
}
