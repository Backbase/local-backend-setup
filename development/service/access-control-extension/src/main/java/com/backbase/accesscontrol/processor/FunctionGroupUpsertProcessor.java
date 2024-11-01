package com.backbase.accesscontrol.processor;

import static com.backbase.accesscontrol.util.errorcodes.AccessControlErrorCodes.ERR_AC_002;

import com.backbase.accesscontrol.domain.dto.ResponseItemExtended;
import com.backbase.accesscontrol.domain.dto.enums.ItemStatusCode;
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
        log.debug("Processing Function Group Upsert for: {}", requestPayload.getName());

        PresentationFunctionGroupPutRequestBodyDto updateDto = mapper.mapToFunctionGroupUpdateDto(requestPayload);
        functionGroupPermissionsUtil.mapBusinessFunctionIds(updateDto, requestPayload);

        ResponseItemExtended response = updateFunctionGroup(updateDto);

        if (isFunctionGroupNotFoundError(response)) {
            log.debug("Function Group not found, creating new Function Group: {}", requestPayload.getName());
            createFunctionGroup(requestPayload);
        }

        log.warn("Upsert Function Group Event processed successfully");
        return requestPayload;
    }

    private ResponseItemExtended updateFunctionGroup(PresentationFunctionGroupPutRequestBodyDto updateDto) {
        return functionGroupServiceFacade.putFunctionGroupsUpdate(Collections.singletonList(updateDto)).get(0);
    }

    private boolean isFunctionGroupNotFoundError(ResponseItemExtended response) {
        return response.getStatus().equals(ItemStatusCode.HTTP_STATUS_BAD_REQUEST) &&
            response.getErrors() != null &&
            !response.getErrors().isEmpty() &&
            response.getErrors().get(0).equalsIgnoreCase(ERR_AC_002.getErrorMessage());
    }

    private void createFunctionGroup(FunctionGroupUpsertDTO requestPayload) {
        FunctionGroupIngest functionGroupIngest = mapper.mapToFunctionGroupIngest(requestPayload);
        functionGroupServiceFacade.postPresentationIngestFunctionGroup(functionGroupIngest);
    }

}
