package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.functiongroup.FunctionGroupIngest;
import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.domain.service.facades.FunctionGroupServiceFacade;
import com.backbase.accesscontrol.mapper.PutFunctionGroupEventMapper;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FunctionGroupUpsertProcessor {

    private final FunctionGroupServiceFacade functionGroupServiceFacade;
    private final PutFunctionGroupEventMapper mapper;

    public FunctionGroupUpsertDTO process(FunctionGroupUpsertDTO requestPayload) {
        try {
            log.debug("Updating Function group: {}", requestPayload.getFunctionGroup().getName());
            PresentationFunctionGroupPutRequestBodyDto updateDto = mapper.mapToFunctionGroupUpdateDto(requestPayload);
            functionGroupServiceFacade.putFunctionGroupsUpdate(Collections.singletonList(updateDto));

            return requestPayload;
        } catch (NotFoundException exception) {
            log.debug("Function group not found: {}", requestPayload.getName());
            FunctionGroupIngest functionGroupIngest = mapper.mapToFunctionGroupIngest(requestPayload);
            log.debug("Creating Function group: {}", functionGroupIngest);
            functionGroupServiceFacade.postPresentationIngestFunctionGroup(functionGroupIngest);
        }

        log.warn("Upsert Function Group Event processed successfully");

        return requestPayload;
    }

}
