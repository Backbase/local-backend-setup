package com.backbase.cucumber.steps.smoketest;

import com.backbase.services.services.Identity;

public class IdentityAuthentication {
    protected final Identity identity = new Identity();

    public void aUserHasAuthenticatedToRealm(String realmName, String clientId) {
        identity.aUserHasAuthenticated(realmName, clientId);
    }
}
