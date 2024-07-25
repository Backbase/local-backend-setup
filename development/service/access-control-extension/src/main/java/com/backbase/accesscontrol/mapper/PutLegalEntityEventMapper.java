package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.dto.legalentity.BatchUpdateLegalEntityItemDto;
import com.backbase.accesscontrol.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityCreateItem;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityPut;
import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutLegalEntityEventMapper {

    CreateLegalEntityRequest mapToCreateLegalEntity(LegalEntityCreateItem createLegalEntity);
    List<BatchUpdateLegalEntityItemDto> mapBatchUpdateLegalEntityItems(List<LegalEntityPut> legalEntityPuts);
}
