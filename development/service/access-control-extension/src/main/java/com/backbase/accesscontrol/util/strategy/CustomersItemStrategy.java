package com.backbase.accesscontrol.util.strategy;

import static com.backbase.accesscontrol.util.ErrorCodes.ERR_IAG_033;
import static com.backbase.accesscontrol.util.ExceptionUtil.getNotFoundException;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.dbs.accesscontrol.api.client.v3.LegalEntitiesApi;
import com.backbase.dbs.accesscontrol.api.client.v3.model.LegalEntityItemBase;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomersItemStrategy implements DataItemExternalIdStrategy {

    private static final String DATA_ITEM_TYPE = "CUSTOMERS";

    private final LegalEntitiesApi legalEntitiesApi;

    @Override
    public String getType() {
        return DATA_ITEM_TYPE;
    }

    @Override
    public String getInternalId(String externalId, String serviceAgreementInternalId) {
        LegalEntityItemBase legalEntityItemBase;
        try {
            legalEntityItemBase = legalEntitiesApi.getLegalEntityByExternalId(externalId);
        } catch (NotFoundException e) {
            log.info("Legal entity with external id {} not found.", externalId);
            throw getNotFoundException(ERR_IAG_033.getErrorMessage(), ERR_IAG_033.getErrorCode());
        }
        return legalEntityItemBase.getId();
    }

    @Override
    public Map<String, String> mapExternalToInternalIds(Set<String> externalIds, String serviceAgreementInternalId) {
        log.info("Get legal entities by external ids: {}", externalIds);
        Map<String, String> result = new HashMap<>();
        for (String externalId : externalIds) {
            result.put(externalId, getInternalId(externalId, serviceAgreementInternalId));
        }
        return result;
    }
}
