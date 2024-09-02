package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.persistence.enums.CustomerCategory;
import com.backbase.accesscontrol.persistence.enums.LegalEntityType;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutLegalEntityEventMapper {

    CreateLegalEntityRequest mapToCreateLegalEntity(LegalEntityCreateItem createLegalEntity);

    default LegalEntityType toLegalEntityDomainType(com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityType updatedLegalEntityType) {
        if (updatedLegalEntityType == null) {
            throw new IllegalArgumentException("specType cannot be null");
        }
        return switch (updatedLegalEntityType) {
            case CUSTOMER -> LegalEntityType.CUSTOMER;
            case BANK -> LegalEntityType.BANK;
        };
    }

    default CustomerCategory toCustomerCategoryDomainType(
        com.backbase.accesscontrol.service.rest.spec.v3.model.CustomerCategory updatedCustomerCategory) {
        if (updatedCustomerCategory == null) {
            throw new IllegalArgumentException("Customer Category cannot be null");
        }
        return switch (updatedCustomerCategory) {
            case RETAIL -> CustomerCategory.RETAIL;
            case BUSINESS -> CustomerCategory.BUSINESS;
        };
    }
}
