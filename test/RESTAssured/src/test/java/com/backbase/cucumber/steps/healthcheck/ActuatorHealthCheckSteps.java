package com.backbase.cucumber.steps.healthcheck;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import com.backbase.config.Constants;
import com.backbase.services.services.AccessControl;
import com.backbase.services.services.ArrangementManager;
import com.backbase.services.services.TokenConverter;
import com.backbase.services.services.UserManager;
import org.apache.commons.lang3.NotImplementedException;

public class ActuatorHealthCheckSteps
{
    private AccessControl accessControl;
    private ArrangementManager arrangementManager;
    private TokenConverter tokenConverter;
    private UserManager userManager;

    @Given("a service has started for {string}")
    public void aServiceHasStartedFor(String serviceName) {
        switch (serviceName) {
            case (Constants.accessControlServiceName) :
                accessControl = new AccessControl();
                break;
            case (Constants.arrangementManagerServiceName) :
                arrangementManager = new ArrangementManager();
                break;
            case (Constants.tokenConverterServiceName) :
                tokenConverter = new TokenConverter();
                break;
            case (Constants.userManagerServiceName) :
                userManager = new UserManager();
                break;
            default:
                String errorMessage = "The service has not been configured, based on the service name: " + serviceName;
                throw new NotImplementedException(errorMessage);
        }
    }

    @When("a healthcheck is requested for service {string}")
    public void aHealthcheckIsRequestedForService(String serviceName) {
        switch (serviceName) {
            case (Constants.accessControlServiceName) :
                accessControl.aHealthCheckIsRequested();
                break;
            case (Constants.arrangementManagerServiceName) :
                arrangementManager.aHealthCheckIsRequested();
                break;
            case (Constants.tokenConverterServiceName) :
                tokenConverter.aHealthCheckIsRequested();
                break;
            case (Constants.userManagerServiceName) :
                userManager.aHealthCheckIsRequested();
                break;
            default:
                String errorMessage = "The service has not been configured, based on the service name: " + serviceName;
                throw new NotImplementedException(errorMessage);
        }
    }

    @Then("the heathcheck returns up for service {string}")
    public void theHeathcheckReturnsUpForService(String serviceName) {
        switch (serviceName) {
            case (Constants.accessControlServiceName) :
                accessControl.theHealthCheckResponseReturnsUP();
                break;
            case (Constants.arrangementManagerServiceName) :
                arrangementManager.theHealthCheckResponseReturnsUP();
                break;
            case (Constants.tokenConverterServiceName) :
                tokenConverter.theHealthCheckResponseReturnsUP();
                break;
            case (Constants.userManagerServiceName) :
                userManager.theHealthCheckResponseReturnsUP();
                break;
            default:
                String errorMessage = "The service has not been configured, based on the service name: " + serviceName;
                throw new NotImplementedException(errorMessage);
        }
    }
}
