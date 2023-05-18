package com.backbase.cucumber.steps.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.ArrangementManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ArrangementManagerSteps extends IdentityAuthentication {
    private final ArrangementManager arrangementManager = new ArrangementManager();

    @Given("a user has authenticated for arrangement manager")
    public void aUserHasAuthenticatedForArrangementManager() {
        aUserHasAuthenticatedToRealm(Constants.backbaseRealmName, Constants.bbToolingClient);
    }

    @When("the User Requests To Get Balances Aggregations")
    public void theUserRequestsToGetBalancesAggregations() {
        arrangementManager.theUserRequestsToGetBalancesAggregations(identity.token);
    }

    @When("the User Requests To Get Product Kinds")
    public void theUserRequestsToGetProductKinds() {
        arrangementManager.theUserRequestsToGetProductKinds(identity.token);
    }

    @Then("the Response Returns As Expected")
    public void theResponseReturnsAsExpected() {
        arrangementManager.theResponseReturnsOK();
    }
}
