package com.backbase.accesscontrol.util.strategy;

import static com.backbase.accesscontrol.util.ErrorCodes.ERR_IAG_032;
import static com.backbase.accesscontrol.util.ExceptionUtil.getNotFoundException;
import static java.util.stream.Collectors.toMap;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.dbs.arrangement.api.client.v2.ArrangementsApi;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItem;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementItems;
import com.backbase.dbs.arrangement.api.client.v2.model.AccountArrangementsFilter;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArrangementItemStrategy implements DataItemExternalIdStrategy {

    private static final String DATA_ITEM_TYPE = "ARRANGEMENTS";

    private final ArrangementsApi arrangementsApi;

    @Override
    public String getType() {
        return DATA_ITEM_TYPE;
    }

    @Override
    public String getInternalId(String externalId, String serviceAgreementId) {
        log.debug("Get arrangement internal id by external id: {}", externalId);

        try {
            return arrangementsApi.getInternalId(externalId).getInternalId();
        } catch (NotFoundException e) {
            log.info("Arrangement with external id {} does not exists.", externalId);
            throw getNotFoundException(ERR_IAG_032.getErrorMessage(), ERR_IAG_032.getErrorCode());
        }
    }

    @Override
    public Map<String, String> mapExternalToInternalIds(Set<String> externalIds, String serviceAgreementId) {
        log.debug("Filter account arrangements items by external ids: {}", externalIds);

        AccountArrangementItems arrangements = arrangementsApi.postFilter(
            new AccountArrangementsFilter().externalArrangementIds(externalIds)
                .size(externalIds.size()));

        if (externalIds.size() != arrangements.getArrangementElements().size()) {
            log.info("Arrangements from arrangement domain have size {} but sent {} for mapping",
                arrangements.getArrangementElements().size(), externalIds.size());
            throw getNotFoundException(ERR_IAG_032.getErrorMessage(), ERR_IAG_032.getErrorCode());
        }

        return arrangements.getArrangementElements().stream()
            .collect(toMap(AccountArrangementItem::getExternalArrangementId, AccountArrangementItem::getId));
    }

}
