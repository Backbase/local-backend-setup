package com.backbase.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.Identity;
import org.junit.Test;

public class UserTests extends Identity {
    private final Identity identity = new Identity();

    @Test
    public void canMakeUserRequests() {
        identity.aUserHasAuthenticated(Constants.masterRealmName, Constants.adminCliClientId);
        identity.theUserMakesACreateUserRequest();
        identity.theUserMakesAGetUsersRequest();
        identity.theUserMakesADeleteUserRequest();
    }
}
