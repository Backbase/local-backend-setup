package com.backbase.datagroup.util.strategy;

import java.util.Map;
import java.util.Set;

public interface DataItemExternalIdStrategy {

    /**
     * Returns the internal id of the data item for external id provided.
     *
     * @param externalId         external id of the data item
     * @return internal id of the data item.
     */
    String getInternalId(String externalId, String serviceAgreementInternalId);

    /**
     * Returns the internal ids of the data item for external ids provided.
     *
     * @param externalIds        external ids of the data items.
     * @return map of external id as key and internal id as value of the data items.
     */
    Map<String, String> mapExternalToInternalIds(Set<String> externalIds, String serviceAgreementInternalId);

    /**
     * Returns the data group items type.
     *
     * @return name of the type
     */
    String getType();
}
