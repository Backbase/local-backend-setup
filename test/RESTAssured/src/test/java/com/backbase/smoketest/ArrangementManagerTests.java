package com.backbase.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.ArrangementManager;
import com.backbase.services.services.Identity;
import org.junit.Test;

public class ArrangementManagerTests {
    private final ArrangementManager arrangementManager = new ArrangementManager();
    private final Identity identity = new Identity();

    @Test
    public void canGetBalancesAggregations() {
        identity.aUserHasAuthenticated(Constants.backbaseRealmName, Constants.bbToolingClient);
        arrangementManager.theUserRequestsToGetBalancesAggregations(identity.token);
        arrangementManager.theResponseReturnsOK();
    }

    @Test
    public void canGetProductKinds() {
        identity.aUserHasAuthenticated(Constants.backbaseRealmName, Constants.bbToolingClient);
        arrangementManager.theUserRequestsToGetProductKinds(identity.token);
        arrangementManager.theResponseReturnsOK();
    }
}
