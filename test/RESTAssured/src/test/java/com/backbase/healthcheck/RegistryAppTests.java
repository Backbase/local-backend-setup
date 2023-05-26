package com.backbase.healthcheck;

import com.backbase.services.services.Registry;
import org.junit.Test;

public class RegistryAppTests
{
    private final Registry registry = new Registry();

    @Test
    public void accessControlHealthCheckReturnsUP() {
        registry.appDetailsAreRequestedForService("access-control");
        registry.theResponseReturnsOK();
        registry.theAppResponseReturnsUP();
    }

    @Test
    public void arrangementManagerHealthCheckReturnsUP() {
        registry.appDetailsAreRequestedForService("arrangement-manager");
        registry.theResponseReturnsOK();
        registry.theAppResponseReturnsUP();
    }

    @Test
    public void tokenConverterHealthCheckReturnsUP() {
        registry.appDetailsAreRequestedForService("token-converter");
        registry.theResponseReturnsOK();
        registry.theAppResponseReturnsUP();
    }

    @Test
    public void userManagerHealthCheckReturnsUP() {
        registry.appDetailsAreRequestedForService("user-manager");
        registry.theResponseReturnsOK();
        registry.theAppResponseReturnsUP();
    }
}
