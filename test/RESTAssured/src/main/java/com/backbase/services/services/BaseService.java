package com.backbase.services.services;

import com.backbase.config.Constants;
import com.backbase.util.ApplicationHttpUtils;
import io.restassured.response.Response;
import org.junit.Assert;

public class BaseService {
    protected String serviceName;
    protected ApplicationHttpUtils applicationHttpUtils;
    protected Response response;

    public BaseService(String serviceName, String baseServiceUrl) {
        this.serviceName = serviceName;
        applicationHttpUtils = new ApplicationHttpUtils(baseServiceUrl);
    }

    public void aHealthCheckIsRequested() {
        String healthCheckUrl = Constants.baseUrl + "/api/" + serviceName + Constants.edgeActuatorHealthPath;
        response = applicationHttpUtils.getUrl(healthCheckUrl);
    }

    public void theResponseReturnsOK() {
        theResponseReturnsCode(200);
    }

    public void theResponseReturnsCode(int statusCode) {
        Assert.assertNotNull(response);
        Assert.assertEquals(statusCode, response.statusCode());
    }

    public void theHealthCheckResponseReturnsUP() {
        Assert.assertNotNull(response);
        String responseBody = ApplicationHttpUtils.getResponseBody(response);

        Assert.assertEquals(Constants.upHealthCheckResponseBody, responseBody);
    }
}
