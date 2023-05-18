package com.backbase.services.services;

import com.backbase.config.Constants;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

public class ArrangementManager extends BaseService {

    public ArrangementManager() {
        super(Constants.arrangementManagerServiceName, Constants.baseUrl + Constants.arrangementManagerPath);
    }

    public void theUserRequestsToGetBalancesAggregations(String token) {
        String aggregationsPath = "/v2/balances/aggregations";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(aggregationsPath, authHeader, ContentType.URLENC);
    }

    public void theUserRequestsToGetProductKinds(String token) {
        String productKindsPath = "/v2/product-kinds";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(productKindsPath, authHeader, ContentType.URLENC);
    }
}
