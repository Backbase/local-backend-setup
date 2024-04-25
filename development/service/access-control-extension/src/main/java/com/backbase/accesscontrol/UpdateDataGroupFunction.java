package com.backbase.accesscontrol;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.mappers.model.PayloadConverter;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationDataGroupUpdate;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationItemIdentifier;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.dbs.arrangement.api.client.v2.ArrangementsApi;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItem;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItems;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementsFilter;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("updateDataGroup")
@AllArgsConstructor
@Slf4j
public class UpdateDataGroupFunction implements
    Function<Message<IntegrationDataGroupItemBatchPutRequestBody>, IntegrationDataGroupItemBatchPutRequestBody> {

    private static final String NOT_FOUND_MESSAGE = "Not Found";

    private final ArrangementsApi arrangementsApi;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper putDataGroupsEventMapper;

    /**
     * Applies this function to the given argument.
     *
     * @param message the function argument
     * @return the function result
     */
    @Override
    public IntegrationDataGroupItemBatchPutRequestBody apply(
        Message<IntegrationDataGroupItemBatchPutRequestBody> message) {
        log.info("Message received: {}", message);
        Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
        var requestPayload = message.getPayload();

        PresentationDataGroupUpdate dataGroupUpdate = putDataGroupsEventMapper
            .map(message.getPayload());
        if (containsExternalItemIdentifier(requestPayload.getDataItems())) {
            List<String> internalIds = mapExternalToInternalIds(requestPayload.getDataItems().stream()
                .map(IntegrationItemIdentifier::getExternalIdIdentifier)
                .collect(Collectors.toSet()));
            dataGroupUpdate.setDataItems(internalIds.stream().map(id -> new PresentationItemIdentifier().id(id))
                .toList());
        }
        PresentationDataGroupUpdateDto dto = mapEventToDto(dataGroupUpdate);
        dataGroupServiceFacade.updateDataGroup(dto);

        log.info("Event process successfully");

        if (acknowledgment != null) {
            System.out.println("Acknowledgment provided");
            acknowledgment.acknowledge();
        }

        return message.getPayload();
    }

    private PresentationDataGroupUpdateDto mapEventToDto(PresentationDataGroupUpdate presentationDataGroupUpdate) {
        return putDataGroupsEventMapper.map(presentationDataGroupUpdate);
    }

    public boolean containsExternalItemIdentifier(
        List<com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier> list) {
        if (list != null) {
            for (com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier dataItem : list) {
                if (dataItem != null && dataItem.getExternalIdIdentifier() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> mapExternalToInternalIds(Set<String> externalIds) {
        log.info("Filter account arrangements items by external ids: {}", externalIds);

        AccountArrangementItems arrangements = arrangementsApi.postFilter(
            new AccountArrangementsFilter().externalArrangementIds(externalIds)
                .size(externalIds.size()));

        if (externalIds.size() != arrangements.getArrangementElements().size()) {
            log.info("Arrangements from arrangement domain have size {} but sent {} for mapping",
                arrangements.getArrangementElements().size(), externalIds.size());
            throw getNotFoundException("Arrangement not found", "dataGroup.external.items.error.message.ARRANGEMENT_NOT_FOUND");
        }

        return arrangements.getArrangementElements().stream()
            .map(AccountArrangementItem::getId)
            .toList();
    }

    /**
     * Returns SDK Not Found Exception.
     *
     * @param errorMessage - string containing the error message
     * @param errorCode    - string containing the error code
     * @return {@link NotFoundException}
     */
    public static NotFoundException getNotFoundException(String errorMessage, String errorCode) {
        log.warn("Not found exception with message {} and code {}", errorMessage, errorCode);
        return new NotFoundException().withMessage(NOT_FOUND_MESSAGE)
            .withErrors(Collections.singletonList(new Error().withMessage(errorMessage).withKey(errorCode)));
    }
}
