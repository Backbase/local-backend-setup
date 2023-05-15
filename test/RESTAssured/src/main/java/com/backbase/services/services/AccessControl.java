package com.backbase.services.services;

import com.backbase.config.Constants;
import com.backbase.domain.ServiceAgreement;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

public class AccessControl extends BaseService {

    public AccessControl() {
        super(Constants.accessControlServiceName, Constants.baseUrl + Constants.accessControlPath);
    }

    public void theUserRequestsToGetDataGroups(String token) {
        String dataGroupsUrl = "/v3/accessgroups/data-groups?serviceAgreementId=" + getMsaId(token);
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(dataGroupsUrl, authHeader, ContentType.JSON);
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

    public void theUserRequestsToGetFunctionGroups(String token) {
        String functionGroupsPath = "/v3/accessgroups/function-groups?serviceAgreementId=" + getMsaId(token);
        Header authHeader = new Header("Authorization", "Bearer " + token);
        String requestBody =
            "{\n" +
                "  \"name\": \"Function group\",\n" +
                "  \"description\": \"Some description for Function group\",\n" +
                "  \"serviceAgreementId\": \"0889e686d31e4216b3dd5d66163d2b14\",\n" +
                "  \"approvalTypeId\": \"eb00b7da-e360-483a-b383-0591ea9de464\",\n" +
                "  \"permissions\": [\n" +
                "    {\n" +
                "      \"functionId\": \"1\",\n" +
                "      \"assignedPrivileges\": [\n" +
                "        {\n" +
                "          \"privilege\": \"view\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"privilege\": \"execute\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"functionId\": \"2\",\n" +
                "      \"assignedPrivileges\": [\n" +
                "        {\n" +
                "          \"privilege\": \"view\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"validFromDate\": \"2017-03-31\",\n" +
                "  \"validFromTime\": \"07:48:23\",\n" +
                "  \"validUntilDate\": \"2020-03-31\",\n" +
                "  \"validUntilTime\": \"07:48:23\"\n" +
                "}";

        response = applicationHttpUtils.get(functionGroupsPath, authHeader, requestBody);
    }

    private String getMsaId(String token) {
        String userContextServiceAgreementsPath = "/v3/accessgroups/user-context/service-agreements";
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(userContextServiceAgreementsPath, authHeader, ContentType.URLENC);

        ServiceAgreement[] serviceAgreements = response.getBody().as(ServiceAgreement[].class);

        return serviceAgreements[0].id;
    }
}
