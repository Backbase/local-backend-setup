package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.aps.ApsIdentifiersDto;
import com.backbase.accesscontrol.domain.dto.enums.ServiceAgreementStatus;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementItemPutDto;
import com.backbase.accesscontrol.domain.dto.serviceagreement.ServiceAgreementParticipantDto;
import com.backbase.accesscontrol.persistence.enums.CustomerCategory;
import com.backbase.accesscontrol.service.rest.spec.v3.model.CreateStatus;
import com.backbase.integration.accessgroup.rest.spec.v3.Participant;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;
import com.backbase.integration.accessgroup.rest.spec.v3.UserApsIdentifiers;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PutServiceAgreementEventMapper {

    ServiceAgreementItemPutDto mapToServiceAgreementUpdateDto(ServiceAgreement event);

    ServiceAgreementDto mapToServiceAgreementCreateDto(ServiceAgreement event);

    default List<ServiceAgreementParticipantDto> mapParticipants(List<Participant> participants) {
        return participants.stream()
            .map(participant -> {
                ServiceAgreementParticipantDto dto = new ServiceAgreementParticipantDto();
                dto.setExternalId(participant.getExternalId());
                dto.setSharingUsers(participant.getSharingUsers());
                dto.setSharingAccounts(participant.getSharingAccounts());
                dto.setAdmins(new LinkedHashSet<>(participant.getAdmins()));
                dto.setUsers(new LinkedHashSet<>(participant.getUsers()));
                return dto;
            })
            .toList();
    }

    default ApsIdentifiersDto mapUserApsIdentifiers(UserApsIdentifiers apsIdentifiers) {
        if (apsIdentifiers == null) {
            return null;
        }
        ApsIdentifiersDto dto = new ApsIdentifiersDto();
        dto.setNameIdentifiers(new LinkedHashSet<>(apsIdentifiers.getNameIdentifiers()));
        dto.setIdIdentifiers(new LinkedHashSet<>(apsIdentifiers.getIdIdentifiers()));
        return dto;
    }

}
