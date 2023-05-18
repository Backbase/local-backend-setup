package com.backbase.services.services;

import com.backbase.config.Constants;
import com.backbase.util.ApplicationHttpUtils;
import org.junit.Assert;

public class Registry extends BaseService {

    public Registry() { super("eureka", Constants.registryUrl); }

    public void appDetailsAreRequestedForService(String serviceName) {
        String healthCheckPath = Constants.registryAppPath + serviceName;
        response = applicationHttpUtils.get(healthCheckPath);
    }

    public void theAppResponseReturnsUP() {
        Assert.assertNotNull(response);
        String responseBody = ApplicationHttpUtils.getResponseBody(response);

        Assert.assertTrue(responseBody.contains(Constants.upRegistryAppResponseBody));
    }
}
