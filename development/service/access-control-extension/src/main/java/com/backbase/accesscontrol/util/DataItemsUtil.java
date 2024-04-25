package com.backbase.accesscontrol.util;

import com.backbase.accesscontrol.domain.dto.DataGroupBaseDto;
import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.domain.dto.PresentationItemIdentifierDto;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.dbs.arrangement.api.client.v2.ArrangementsApi;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItem;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItems;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementsFilter;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataItemsUtil {

    private static final String NOT_FOUND_MESSAGE = "Not Found";

    private final ArrangementsApi arrangementsApi;

    public void updateDataItems(List<IntegrationItemIdentifier> dataItems, PresentationDataGroupUpdateDto dto) {
        if (containsExternalItemIdentifier(dataItems)) {
            List<String> internalIds = mapExternalToInternalIds(dataItems.stream()
                .map(IntegrationItemIdentifier::getExternalIdIdentifier)
                .collect(Collectors.toSet()));
            dto.setDataItems(internalIds.stream().map(id -> {
                    var itemDto = new PresentationItemIdentifierDto();
                    itemDto.setInternalIdIdentifier(id);
                    return itemDto;
                })
                .toList());
        }
    }

    public void updateDataItems(List<IntegrationItemIdentifier> dataItems, DataGroupBaseDto dto) {
        if (containsExternalItemIdentifier(dataItems)) {
            List<String> internalIds = mapExternalToInternalIds(dataItems.stream()
                .map(IntegrationItemIdentifier::getExternalIdIdentifier)
                .collect(Collectors.toSet()));
            dto.setItems(internalIds);
        }
    }

    private boolean containsExternalItemIdentifier(List<IntegrationItemIdentifier> list) {
        return list != null && list.stream()
            .anyMatch(dataItem -> dataItem != null && dataItem.getExternalIdIdentifier() != null);
    }

    public List<String> mapExternalToInternalIds(Set<String> externalIds) {
        log.info("Filter account arrangement items by external ids: {}", externalIds);

        AccountArrangementItems arrangements = arrangementsApi.postFilter(
            new AccountArrangementsFilter().externalArrangementIds(externalIds)
                .size(externalIds.size()));

        if (externalIds.size() != arrangements.getArrangementElements().size()) {
            log.info("Arrangements from arrangement domain have size {} but sent {} for mapping",
                arrangements.getArrangementElements().size(), externalIds.size());
            throw getNotFoundException("Arrangement not found",
                "dataGroup.external.items.error.message.ARRANGEMENT_NOT_FOUND");
        }

        return arrangements.getArrangementElements().stream()
            .map(AccountArrangementItem::getId)
            .toList();
    }

    public static NotFoundException getNotFoundException(String errorMessage, String errorCode) {
        log.warn("Not found exception with message {} and code {}", errorMessage, errorCode);
        return new NotFoundException().withMessage(NOT_FOUND_MESSAGE)
            .withErrors(Collections.singletonList(new Error().withMessage(errorMessage).withKey(errorCode)));
    }

}
