package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.dto.ServiceAgreementDto;
import com.backbase.accesscontrol.dto.ServiceAgreementItemPutDto;
import com.backbase.integration.accessgroup.rest.spec.v3.ServiceAgreement;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutServiceAgreementsEventMapper {

    ServiceAgreementItemPutDto mapToServiceAgreementItemPutDto(ServiceAgreement requestPayload);
    ServiceAgreementDto mapToDataGroupBaseDto(ServiceAgreement requestPayload);

}
