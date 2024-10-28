package com.backbase.accesscontrol.processor;

import com.backbase.accesscontrol.domain.dto.ResponseItemExtended;
import com.backbase.accesscontrol.domain.dto.functiongroup.FunctionGroupIngest;
import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.domain.service.facades.FunctionGroupServiceFacade;
import com.backbase.accesscontrol.mapper.PutFunctionGroupEventMapper;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.accesscontrol.util.FunctionGroupPermissionsUtil;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FunctionGroupUpsertProcessor {

    private final FunctionGroupServiceFacade functionGroupServiceFacade;
    private final FunctionGroupPermissionsUtil functionGroupPermissionsUtil;

    private final PutFunctionGroupEventMapper mapper;

    public FunctionGroupUpsertDTO process(FunctionGroupUpsertDTO requestPayload) {
        //TODO it may change it according to payload difference. Check!
        if (isFunctionGroupUpdateObjectIsPresent(requestPayload)) {
            log.debug("Updating Function Group: {}", requestPayload);
            PresentationFunctionGroupPutRequestBodyDto updateDto = mapper.mapToFunctionGroupUpdateDto(requestPayload);
            functionGroupPermissionsUtil.mapBusinessFunctionIds(updateDto, requestPayload);
            functionGroupServiceFacade.putFunctionGroupsUpdate(Collections.singletonList(updateDto));
        } else {
            log.debug("Creating Function Group: {}", requestPayload.getName());
            FunctionGroupIngest functionGroupIngest = mapper.mapToFunctionGroupIngest(requestPayload);
            functionGroupServiceFacade.postPresentationIngestFunctionGroup(functionGroupIngest);
        }

        log.warn("Upsert Function Group Event processed successfully");

        return requestPayload;
    }

    private boolean isFunctionGroupUpdateObjectIsPresent(FunctionGroupUpsertDTO functionGroupUpsertDTO) {
        return functionGroupUpsertDTO.getFunctionGroup() != null;
    }

}
