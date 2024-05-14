package com.backbase.datagroup.util.strategy;

import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier;
import java.util.List;

public interface DataItemExternalIdConverter {

    List<String> convertDataItemExternalIdsToInternal(
        List<IntegrationItemIdentifier> dataItems, String type, String serviceAgreementInternalId);

    String getInternalId(String externalId, String type, String serviceAgreementInternalId);

    boolean converterExistsByDataItemType(String dataItemType);
}
