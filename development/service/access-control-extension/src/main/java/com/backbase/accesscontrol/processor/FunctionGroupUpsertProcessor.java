package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.functiongroup.FunctionGroupIngest;
import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.domain.service.facades.FunctionGroupServiceFacade;
import com.backbase.accesscontrol.mapper.PutFunctionGroupEventMapper;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.accesscontrol.persistence.entity.FunctionGroup;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationIdentifier;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FunctionGroupUpsertProcessor {

    private final FunctionGroupServiceFacade functionGroupServiceFacade;
    private final PutFunctionGroupEventMapper mapper;

    public FunctionGroupUpsertDTO process(FunctionGroupUpsertDTO requestPayload) {
        if (isFunctionGroupIdIdentifierExists(requestPayload.getIdentifier())) {
            try {
                Pair<String, FunctionGroup> functionGroupPair =
                    functionGroupServiceFacade.getFunctionGroupById(requestPayload.getIdentifier().getIdIdentifier());
                log.debug("Function Group found: {}", functionGroupPair.getValue().getName());
                PresentationFunctionGroupPutRequestBodyDto updateDto =
                    mapper.mapToFunctionGroupUpdateDto(requestPayload);
                log.debug("Updating Data Group: {}", updateDto);
                functionGroupServiceFacade.putFunctionGroupsUpdate(Collections.singletonList(updateDto));

                return requestPayload;
            } catch (NotFoundException exception) {
                //TODO check if this scenario is possible?
                //if so, should we create the function group again here as well?
                log.error("Function Group found with that id: {}", requestPayload.getIdentifier().getIdIdentifier());
            }
        } else {
            log.debug("Function Group id identifier not found: {}", requestPayload.getName());
            FunctionGroupIngest functionGroupIngest = mapper.mapToFunctionGroupIngest(requestPayload);
            log.debug("Creating Function Group: {}", functionGroupIngest);
            functionGroupServiceFacade.postPresentationIngestFunctionGroup(functionGroupIngest);
        }

        log.warn("Upsert Function Group Event processed successfully");

        return requestPayload;
    }

    private boolean isFunctionGroupIdIdentifierExists(IntegrationIdentifier integrationIdentifier) {
        return StringUtils.isNotBlank(integrationIdentifier.getIdIdentifier());
    }

}
