package com.backbase.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.AccessControl;
import com.backbase.services.services.Identity;
import org.junit.Test;

public class AccessControlTests {

    private final AccessControl accessControl = new AccessControl();
    private final Identity identity = new Identity();

    @Test
    public void canGetDataGroups() {
        identity.aUserHasAuthenticated("backbase", Constants.bbToolingClient);
        accessControl.theUserRequestsToGetDataGroups(identity.token);
        accessControl.theResponseReturnsOK();
    }

    @Test
    public void canGetGetUserContextServiceAgreements() {
        identity.aUserHasAuthenticated("backbase", Constants.bbToolingClient);
        accessControl.theUserRequestsToGetUserContextServiceAgreements(identity.token);
        accessControl.theResponseReturnsOK();
    }

    @Test
    public void canSetUserContextServiceAgreements() {
        identity.aUserHasAuthenticated(Constants.backbaseRealmName, Constants.bbToolingClient);
        accessControl.theUserRequestsToSetUserContextServiceAgreements(identity.token);
        accessControl.theResponseReturnsCode(204);
    }

    @Test
    public void canGetFunctionGroups() {
        identity.aUserHasAuthenticated(Constants.backbaseRealmName, Constants.bbToolingClient);
        accessControl.theUserRequestsToGetFunctionGroups(identity.token);
        accessControl.theResponseReturnsOK();
    }

    @Test
    public void canGetUserPermissionsSummary() {
        identity.aUserHasAuthenticated(Constants.backbaseRealmName, Constants.bbToolingClient);
        accessControl.theUserRequestsToGetUserPermissionsSummary(identity.token);
        accessControl.theResponseReturnsOK();
    }
}
