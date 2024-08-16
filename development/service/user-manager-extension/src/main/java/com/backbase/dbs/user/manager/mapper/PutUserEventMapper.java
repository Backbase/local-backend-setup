package com.backbase.dbs.user.manager.mapper;

import com.backbase.dbs.user.manager.models.v2.IngestionBatchUser;
import com.backbase.integration.usermanager.rest.spec.v3.UserExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PutUserEventMapper {


    @Mapping(source = "externalId", target = "externalId")
    @Mapping(source = ".", target = "user")// Mapping the entire UserExternal object
    IngestionBatchUser mapToIngestionBatchUser(UserExternal userExternal);

    com.backbase.dbs.user.manager.models.v2.UserExternal mapToCreateUser(UserExternal userExternal);
}