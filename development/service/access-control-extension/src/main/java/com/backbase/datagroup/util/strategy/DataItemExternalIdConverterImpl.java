package com.backbase.datagroup.util.strategy;

import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationItemIdentifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataItemExternalIdConverterImpl implements DataItemExternalIdConverter {

    private final Map<String, DataItemExternalIdStrategy> dataItemExternalIdStrategyByType;

    public DataItemExternalIdConverterImpl(List<DataItemExternalIdStrategy> dataItemExternalIdStrategies) {
        this.dataItemExternalIdStrategyByType = dataItemExternalIdStrategies.stream()
            .collect(Collectors.toMap(DataItemExternalIdStrategy::getType, Function.identity()));
    }

    @Override
    public List<String> convertDataItemExternalIdsToInternal(
        List<IntegrationItemIdentifier> dataItems, String type, String serviceAgreementInternalId) {
        Set<String> externalIds = dataItems.stream()
            .map(IntegrationItemIdentifier::getExternalIdIdentifier)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<String, String> internalIdByExternal = dataItemExternalIdStrategyByType.get(type)
            .mapExternalToInternalIds(externalIds, serviceAgreementInternalId);
        Set<String> result = dataItems.stream()
            .map(IntegrationItemIdentifier::getInternalIdIdentifier)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        result.addAll(internalIdByExternal.values());
        return result.stream().toList();
    }

    @Override
    public String getInternalId(String externalId, String type, String serviceAgreementInternalId) {
        return dataItemExternalIdStrategyByType.get(type).getInternalId(externalId, serviceAgreementInternalId);
    }

    @Override
    public boolean converterExistsByDataItemType(String dataItemType) {
        return dataItemExternalIdStrategyByType.containsKey(dataItemType);
    }
}
