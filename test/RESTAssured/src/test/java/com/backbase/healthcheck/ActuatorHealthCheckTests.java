package com.backbase.healthcheck;

import com.backbase.services.AccessControl;
import com.backbase.services.ArrangementManager;
import com.backbase.services.TokenConverter;
import com.backbase.services.UserManager;
import org.junit.Test;

public class ActuatorHealthCheckTests
{
    private final AccessControl accessControl = new AccessControl();
    private final ArrangementManager arrangementManager = new ArrangementManager();
    private final TokenConverter tokenConverter = new TokenConverter();
    private final UserManager userManager = new UserManager();

    @Test
    public void accessControlHealthCheckReturnsUP() {
        accessControl.aHealthCheckIsRequested();
        accessControl.theResponseReturnsOK();
        accessControl.theHealthCheckResponseReturnsUP();
    }

    @Test
    public void arrangementManagerHealthCheckReturnsUP() {
        arrangementManager.aHealthCheckIsRequested();
        arrangementManager.theResponseReturnsOK();
        arrangementManager.theHealthCheckResponseReturnsUP();
    }

    @Test
    public void tokenConverterHealthCheckReturnsUP() {
        tokenConverter.aHealthCheckIsRequested();
        tokenConverter.theResponseReturnsOK();
        tokenConverter.theHealthCheckResponseReturnsUP();
    }

    @Test
    public void userManagerHealthCheckReturnsUP() {
        userManager.aHealthCheckIsRequested();
        userManager.theResponseReturnsOK();
        userManager.theHealthCheckResponseReturnsUP();
    }
}
