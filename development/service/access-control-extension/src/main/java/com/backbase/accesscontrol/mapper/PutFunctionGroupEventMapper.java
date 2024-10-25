package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.functiongroup.FunctionGroupIngest;
import com.backbase.accesscontrol.domain.dto.functiongroup.PersistenceFunctionGroup;
import com.backbase.accesscontrol.domain.dto.functiongroup.PresentationFunctionGroupPutRequestBodyDto;
import com.backbase.accesscontrol.model.FunctionGroupUpsertDTO;
import com.backbase.integration.accessgroup.rest.spec.v3.FunctionGroupItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface PutFunctionGroupEventMapper {

    @Mapping(target = "functionGroup.functionGroupId", ignore = true)
    PresentationFunctionGroupPutRequestBodyDto mapToFunctionGroupUpdateDto(FunctionGroupUpsertDTO event);

    @Mapping(target = "externalServiceAgreementId", source = "identifier.nameIdentifier.externalServiceAgreementId")
    @Mapping(target = "validFrom", source = ".", qualifiedByName = "combineValidFromDateAndTime")
    @Mapping(target = "validUntil", source = ".", qualifiedByName = "combineValidUntilDateAndTime")
    @Mapping(target = "type", source = "type", qualifiedByName = "mapTypeEnum")
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


}