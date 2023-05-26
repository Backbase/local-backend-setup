package com.backbase.healthcheck;

import com.backbase.services.services.Identity;
import org.junit.Test;

public class IdentityConfigTest
{
    private final Identity identity = new Identity();

    @Test
    public void identityConfigCheckReturnsBackbase() {
        identity.anIdentityConfigCheckIsRequested();
        identity.theIdentityConfigCheckReturnsBackbase();
    }
}
