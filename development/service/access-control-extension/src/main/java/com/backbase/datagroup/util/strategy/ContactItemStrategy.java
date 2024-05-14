package com.backbase.datagroup.util.strategy;

import static com.backbase.accesscontrol.util.ExceptionUtil.getBadRequestException;
import static com.backbase.accesscontrol.util.ExceptionUtil.getNotFoundException;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.backbase.accesscontrol.util.ExceptionUtil;
import com.backbase.datagroup.util.AccessGroupIntegrationErrorCodes;
import com.backbase.dbs.contact.api.client.v2.ContactsApi;
import com.backbase.dbs.contact.api.client.v2.model.ContactsInternalIdsFilterPostRequestBody;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactItemStrategy implements DataItemExternalIdStrategy {

    public static final String DATA_ITEM_TYPE = "CONTACTS";
    private final ContactsApi contactsApi;

    @Override
    public String getInternalId(String externalId, String serviceAgreementInternalId) {
        return mapExternalToInternalIds(Set.of(externalId), serviceAgreementInternalId).get(externalId);
    }

    @Override
    public Map<String, String> mapExternalToInternalIds(Set<String> externalIds, String serviceAgreementInternalId) {
        Map<String, List<String>> externalContactIdToInternalIds = contactsApi.postContactsInternalIdsFilter(
            serviceAgreementInternalId,
            new ContactsInternalIdsFilterPostRequestBody().externalContactIds(new ArrayList<>(externalIds)));
        if (externalContactIdToInternalIds.size() != externalIds.size()) {
            log.info(
                "Data group with type CONTACTS cannot be created because following contact external ids are not found: {}",
                Sets.difference(externalIds, externalContactIdToInternalIds.keySet()));
            throw getNotFoundException(AccessGroupIntegrationErrorCodes.ERR_IAG_034.getErrorMessage(), AccessGroupIntegrationErrorCodes.ERR_IAG_034.getErrorCode());
        }
        List<String> contactExternalIdsNotMapped = externalContactIdToInternalIds.entrySet().stream()
            .filter(entry -> isEmpty(entry.getValue())).map(Entry::getKey).toList();
        if (!contactExternalIdsNotMapped.isEmpty()) {
            log.info(
                "Data group with type CONTACTS cannot be created because following contact external ids are not found: {}",
                contactExternalIdsNotMapped);
            throw getNotFoundException(AccessGroupIntegrationErrorCodes.ERR_IAG_034.getErrorMessage(), AccessGroupIntegrationErrorCodes.ERR_IAG_034.getErrorCode());
        }
        List<String> contactExternalIdsMappedToMultipleInternalIds = externalContactIdToInternalIds.entrySet()
            .stream().filter(entry -> entry.getValue().size() > 1).map(Entry::getKey).toList();
        if (!contactExternalIdsMappedToMultipleInternalIds.isEmpty()) {
            log.info(
                "Data group with type CONTACTS cannot be created because following data item external ids are mapped to multiple contacts: {}",
                contactExternalIdsMappedToMultipleInternalIds);
            throw ExceptionUtil.getBadRequestException(AccessGroupIntegrationErrorCodes.ERR_IAG_035.getErrorMessage(), AccessGroupIntegrationErrorCodes.ERR_IAG_035.getErrorCode());
        }
        return externalContactIdToInternalIds.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0)));
    }

    @Override
    public String getType() {
        return DATA_ITEM_TYPE;
    }
}
