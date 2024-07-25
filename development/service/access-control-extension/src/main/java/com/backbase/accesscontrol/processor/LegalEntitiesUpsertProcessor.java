package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.dto.legalentity.BatchUpdateLegalEntityItemDto;
import com.backbase.accesscontrol.dto.legalentity.CreateLegalEntityRequest;
import com.backbase.accesscontrol.mapper.PutLegalEntityEventMapper;
import com.backbase.accesscontrol.service.facades.v3.LegalEntityServiceFacade;
import com.backbase.accesscontrol.service.rest.spec.v3.model.LegalEntityPut;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegalEntitiesUpsertProcessor {

    private final LegalEntityServiceFacade legalEntityServiceFacade;
    private final PutLegalEntityEventMapper mapper;

    public List<LegalEntityPut> process(List<LegalEntityPut> requestPayload) {
        try {
            List<BatchUpdateLegalEntityItemDto> batchUpdateLegalEntityItemDtoList =
                mapper.mapBatchUpdateLegalEntityItems(requestPayload);
            requestPayload.forEach(legalEntityItem -> {
                try {
                    // Pipeline for Existing Legal Entity
                    var legalEntity =
                        legalEntityServiceFacade.getLegalEntityByExternalId(legalEntityItem.getCurrentExternalId());
                    log.debug("Legal Entity found: {}", legalEntity.getName());
                } catch (NotFoundException e) {
                    // Pipeline for Creating a New Legal Entity
                    log.debug("Legal Entity not found, creating new one for: {}",
                        legalEntityItem.getNewValues().getName());
                    CreateLegalEntityRequest createLegalEntityRequest =
                        mapper.mapToCreateLegalEntity(legalEntityItem.getNewValues());
                    legalEntityServiceFacade.createLegalEntity(createLegalEntityRequest);
                    batchUpdateLegalEntityItemDtoList.removeIf(dto -> dto.currentExternalId().equals(legalEntityItem.getCurrentExternalId()));
                }
            });
            legalEntityServiceFacade.updateBatchLegalEntities(batchUpdateLegalEntityItemDtoList);
        } catch (Exception e) {
            // Handle any unexpected exceptions
            e.printStackTrace();
        }
        return requestPayload;
    }

}
