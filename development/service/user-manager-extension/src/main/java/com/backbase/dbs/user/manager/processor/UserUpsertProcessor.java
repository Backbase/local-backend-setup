package com.backbase.dbs.user.manager.processor;

import com.backbase.dbs.user.manager.mapper.PutUserEventMapper;
import com.backbase.dbs.user.manager.models.v2.IngestionBatchUser;
import com.backbase.dbs.user.manager.service.UserIntegrationService;
import com.backbase.integration.usermanager.rest.spec.v3.UserExternal;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpsertProcessor {

    private final UserIntegrationService userIntegrationService;
    private PutUserEventMapper putUserEventMapper;

    public void process(UserExternal requestPayload) {

        var searchResult = userIntegrationService.getUserByItsExternalId(requestPayload.getExternalId());
        if (searchResult != null) {
            log.debug("User found: {}", searchResult.getFullName());
            IngestionBatchUser updateDto = putUserEventMapper.mapToIngestionBatchUser(requestPayload);
            log.debug("Updating User: {}", updateDto);
            userIntegrationService.updateUserInBatch(Collections.singletonList(updateDto));
        } else {
            log.debug("User not found with this externalId: {}", requestPayload.getExternalId());
            com.backbase.dbs.user.manager.models.v2.UserExternal createDto = putUserEventMapper.mapToCreateUser(requestPayload);
            log.debug("Creating a new User: {}", createDto);
            userIntegrationService.createUser(createDto);
        }
    }

}
