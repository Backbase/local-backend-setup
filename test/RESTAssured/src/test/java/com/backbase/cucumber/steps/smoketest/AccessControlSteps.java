package com.backbase.cucumber.steps.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.AccessControl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccessControlSteps extends IdentityAuthentication {

    private final AccessControl accessControl = new AccessControl();

    @Given("a user has authenticated for access control")
    public void aUserHasAuthenticatedForAccessControl() {
        aUserHasAuthenticatedToRealm(Constants.backbaseRealmName, Constants.bbToolingClient);
    }

    @When("the User Requests To Get User Context Service Agreements")
    public void theUserRequestsToGetUserContextServiceAgreements() {
        accessControl.theUserRequestsToGetUserContextServiceAgreements(identity.token);
    }

    @When("the User Requests To Set User Context Service Agreements")
    public void theUserRequestsToSetUserContextServiceAgreements() {
        accessControl.theUserRequestsToSetUserContextServiceAgreements(identity.token);
    }

    @When("the User Requests To Get User Permissions Summary")
    public void theUserRequestsToGetUserPermissionsSummary() {
        accessControl.theUserRequestsToGetUserPermissionsSummary(identity.token);
    }

    @Then("the Response Returns OK")
    public void theResponseReturnsOK() {
        accessControl.theResponseReturnsOK();
    }

    @Then("the Response Returns No Content Success")
    public void theResponseReturns204() {
        accessControl.theResponseReturnsCode(204);
    }
}
