package com.backbase.cucumber.steps.healthcheck;

import com.backbase.services.services.Registry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RegistryAppSteps
{
    private Registry registry;

    @Given("a registry service has started")
    public void aRegistryServiceHasStarted() {
        registry = new Registry();
    }

    @When("a registry app check is requested for service {string}")
    public void aRegistryAppCheckIsRequestedForService(String serviceName) {
        registry.appDetailsAreRequestedForService(serviceName);
    }

    @Then("the registry app check returns up")
    public void theRegistryAppCheckReturnsUpForService() {
        registry.theResponseReturnsOK();
        registry.theAppResponseReturnsUP();
    }
}
