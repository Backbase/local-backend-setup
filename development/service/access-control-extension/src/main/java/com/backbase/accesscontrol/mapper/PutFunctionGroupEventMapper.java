package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.functiongroup.FunctionGroupIngest;
import com.backbase.accesscontrol.domain.dto.functiongroup.PersistenceFunctionGroup;
import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.domain.dto.userpermission.Permission;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.integration.accessgroup.rest.spec.v3.FunctionGroupItem;
import com.backbase.integration.accessgroup.rest.spec.v3.Privilege;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface PutFunctionGroupEventMapper {

    PresentationFunctionGroupPutRequestBodyDto mapToFunctionGroupUpdateDto(FunctionGroupUpsertDTO event);

    @Mapping(target = "validFrom", source = ".", qualifiedByName = "combineValidFromDateAndTime")
    @Mapping(target = "validUntil", source = ".", qualifiedByName = "combineValidUntilDateAndTime")
    @Mapping(target = "type", source = "type", qualifiedByName = "mapTypeEnum")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "mapPermissions")
    FunctionGroupIngest mapToFunctionGroupIngest(FunctionGroupUpsertDTO event);

    @Named("mapTypeEnum")
    @ValueMapping(source = "REGULAR", target = "DEFAULT")
    PersistenceFunctionGroup.Type mapTypeEnum(FunctionGroupItem.TypeEnum type);

    @Named("combineValidFromDateAndTime")
    default Date combineValidFromDateAndTime(FunctionGroupUpsertDTO event) {
        return combineDateTime(event.getValidFromDate(), event.getValidFromTime());
    }

    @Named("combineValidUntilDateAndTime")
    default Date combineValidUntilDateAndTime(FunctionGroupUpsertDTO event) {
        return combineDateTime(event.getValidUntilDate(), event.getValidUntilTime());
    }

    default Date combineDateTime(String date, String time) {
        if (date == null || time == null) {
            return null;
        }
        try {
            String dateTimeStr = date + " " + time;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }

    @Named("mapPermissions")
    default List<Permission> mapPermissions(
        List<com.backbase.integration.accessgroup.rest.spec.v3.Permission> permissions) {
        return permissions.stream()
            .map(p -> {
                Permission permission = new Permission();
                permission.setFunctionId(p.getFunctionId());

                permission.setPrivileges(
                    p.getAssignedPrivileges().stream()
                        .map(Privilege::getPrivilege)
                        .toList()
                );

                return permission;
            })
            .toList();
    }

}