package com.backbase.services.services;

import com.backbase.config.Constants;
import com.backbase.domain.ServiceAgreement;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

public class AccessControl extends BaseService {

    public AccessControl() {
        super(Constants.accessControlServiceName, Constants.baseUrl + Constants.accessControlPath);
    }

    public void theUserRequestsToGetUserContextServiceAgreements(String token) {
        String userContextServiceAgreementsPath = "/v3/accessgroups/user-context/service-agreements";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(userContextServiceAgreementsPath, authHeader, ContentType.URLENC);
    }

    public void theUserRequestsToGetUserPermissionsSummary(String token) {
        String userPermissionsSummaryPath = "/v3/accessgroups/users/permissions/summary";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(userPermissionsSummaryPath, authHeader, ContentType.URLENC);
    }

    public void theUserRequestsToSetUserContextServiceAgreements(String token) {
        String userContextServiceAgreementsPath = "/v2/accessgroups/usercontext";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        String requestBody =
            "{\n" +
                "    \"serviceAgreementId\": \"" + getMsaId(token) + "\"\n" +
                "}";
        response = applicationHttpUtils.post(userContextServiceAgreementsPath, authHeader, requestBody);
    }

    private String getMsaId(String token) {
        String userContextServiceAgreementsPath = "/v3/accessgroups/user-context/service-agreements";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(userContextServiceAgreementsPath, authHeader, ContentType.URLENC);

        ServiceAgreement[] serviceAgreements = response.getBody().as(ServiceAgreement[].class);

        return serviceAgreements[0].id;
    }
}
