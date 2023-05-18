package com.backbase.cucumber.steps.healthcheck;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import com.backbase.services.services.Identity;

public class IdentityConfigSteps
{
    private Identity identity;

    @Given("an Identity service has started")
    public void anIdentityServiceHasStarted() {
        identity = new Identity();
    }

    @When("an Identity config check is requested")
    public void anIdentityConfigCheckIsRequested() {
        identity.anIdentityConfigCheckIsRequested();
    }

    @Then("the Identity config check returns as expected")
    public void theIdentityConfigCheckReturnsAsExpected() {
        identity.theIdentityConfigCheckReturnsBackbase();
    }
}
