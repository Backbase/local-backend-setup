package com.backbase.accesscontrol.util;

import static com.backbase.accesscontrol.util.ExceptionUtil.NOT_FOUND_MESSAGE;
import static com.backbase.accesscontrol.util.errorcodes.AccessControlErrorCodes.ERR_FG_016;

import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.domain.dto.userpermission.Permission;
import com.backbase.accesscontrol.domain.service.business.businessfunction.BusinessFunctionsService;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationPermissionFunctionGroupUpdate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FunctionGroupPermissionsUtil {

    private final BusinessFunctionsService businessFunctionsService;

    public void mapBusinessFunctionIds(PresentationFunctionGroupPutRequestBodyDto updateDto,
                                       FunctionGroupUpsertDTO upsertRequestDto) {
        log.debug("Mapping business function ids by function names.");

        if (upsertRequestDto.getFunctionGroup() != null
            && containsFunctionName(upsertRequestDto.getFunctionGroup().getPermissions())) {

            List<Permission> permissionsToUpdate = upsertRequestDto.getFunctionGroup().getPermissions().stream()
                .map(p -> {
                    String functionId = businessFunctionsService.getBusinessFunctionIdByName(p.getFunctionName());

                    if (functionId == null) {
                        log.debug("The business function not found for the business function name: {}", p.getFunctionName());
                        throw new NotFoundException().withMessage("Business Function not found")
                            .withErrors(Collections.singletonList(
                                new Error().withMessage(ERR_FG_016.getErrorMessage()).withKey(ERR_FG_016.getErrorCode())));
                    }

                    Permission newPermission = new Permission();
                    newPermission.setFunctionId(functionId);
                    newPermission.setPrivileges(Objects.requireNonNullElse(p.getPrivileges(), Collections.emptyList()));
                    return newPermission;
                })
                .toList();

            updateDto.getFunctionGroup().setPermissions(permissionsToUpdate);
        }
    }

    private boolean containsFunctionName(List<IntegrationPermissionFunctionGroupUpdate> permissionList) {
        return permissionList != null && permissionList.stream()
            .anyMatch(permissionUpdateItem -> permissionUpdateItem != null
                && permissionUpdateItem.getFunctionName() != null);
    }

    public static NotFoundException getNotFoundException(String errorMessage, String errorCode) {
        log.warn("Not found exception with message {} and code {}", errorMessage, errorCode);
        return new NotFoundException().withMessage(NOT_FOUND_MESSAGE)
            .withErrors(Collections.singletonList(new Error().withMessage(errorMessage).withKey(errorCode)));
    }

}
