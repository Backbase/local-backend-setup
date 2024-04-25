package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationItemIdentifierDto;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationDataGroupUpdate;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationItemIdentifier;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutDataGroupsEventMapper {

    @Mapping(target = "dataItems", source = "dataItems", qualifiedByName = "mapDataItems")
    PresentationDataGroupUpdateDto map(PresentationDataGroupUpdate event);

    @Mapping(target = "dataItems", ignore = true)
    PresentationDataGroupUpdate map(IntegrationDataGroupItemBatchPutRequestBody event);


    @Named("mapDataItems")
    default List<PresentationItemIdentifierDto> mapDataItems(List<PresentationItemIdentifier> dataItems) {

        return dataItems.stream()
            .map(PresentationItemIdentifier::getId)
            .map(id -> {
                var obj = new PresentationItemIdentifierDto();
                obj.setInternalIdIdentifier(id);
                return obj;
            })
            .toList();
    }

}
