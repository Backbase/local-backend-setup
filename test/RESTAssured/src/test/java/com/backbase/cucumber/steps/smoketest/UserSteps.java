package com.backbase.cucumber.steps.smoketest;

import com.backbase.config.Constants;
import com.backbase.services.services.Identity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Test;

public class UserSteps extends IdentityAuthentication {
    private final Identity identity = new Identity();

    @Given("a user has authenitcated")
    public void aUserHasAuthenitcated() {
        aUserHasAuthenitcatedToRealm(Constants.masterRealmName, Constants.adminCliClientId);
    }

    @Given("the User Makes A Create Users Request")
    public void theUserMakesACreateUsersRequest() {
        identity.theUserMakesACreateUserRequest();
    }

    @Test
    public void canMakeUserRequests() {
        identity.aUserHasAuthenticated(Constants.masterRealmName, Constants.adminCliClientId);
    }

    @When("the User Makes A Get Users Request")
    public void theUserMakesAGetUsersRequest() {
        identity.theUserMakesAGetUsersRequest();
    }

    @When("the User Makes A Delete Users Request")
    public void theUserMakesADeleteUsersRequest() {
        identity.theUserMakesADeleteUserRequest();
    }

    @Then("the Response Returns {int}")
    public void theResponseReturns(int statusCode) {
        // Checked in the related
    }

}
