package com.backbase.accesscontrol.mapper;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationItemIdentifierDto;
import com.backbase.accesscontrol.event.event.spec.v1.DataItem;
import com.backbase.accesscontrol.event.event.spec.v1.DataItem__1;
import com.backbase.accesscontrol.event.event.spec.v1.PutDataGroupsEvent;
import com.backbase.accesscontrol.event.event.spec.v1.PutDataGroupsSuccessEvent;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationDataGroupUpdate;
import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PutDataGroupsEventMapper {

    @Mapping(target = "dataItems", source = "dataItems", qualifiedByName = "mapDataItems")
    PresentationDataGroupUpdateDto map(PutDataGroupsEvent event);

    @Mapping(target = "dataItems", source = "dataItems", qualifiedByName = "mapDtoDataItems")
    PutDataGroupsSuccessEvent map(PresentationDataGroupUpdateDto dto);

    @Named("mapDataItems")
    default List<PresentationItemIdentifierDto> mapDataItems(List<DataItem> dataItems) {

        return dataItems.stream()
            .map(DataItem::getId)
            .map(id -> {
                var obj = new PresentationItemIdentifierDto();
                obj.setInternalIdIdentifier(id);
                return obj;
            })
            .toList();
    }

    @Named("mapDtoDataItems")
    default List<DataItem__1> mapDtoDataItems(List<PresentationItemIdentifierDto> dtoDataItems) {

        return dtoDataItems.stream()
            .map(PresentationItemIdentifierDto::getInternalIdIdentifier)
            .map(id -> {
                var obj = new DataItem__1();
                obj.setId(id);
                return obj;
            })
            .toList();
    }
}
