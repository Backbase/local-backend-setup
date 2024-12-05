package com.backbase.util;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;

import com.backbase.config.Constants;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class ApplicationHttpUtils {

    private final String baseUrl;

    public ApplicationHttpUtils(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static String getResponseBody(Response httpResponse) {
        return httpResponse.getBody().asString();
    }

    public Response identityLogin(final String realm, final String clientId) {
        String identityLoginUrl = Constants.identityUrl + Constants.identityRealmPath + realm + Constants.identityAuthPath;

        return given()
            .config(config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .formParam("username", "admin")
            .formParam("password", "admin")
            .formParam("grant_type", "password")
            .formParam("client_id", clientId)
            .post(identityLoginUrl);
    }

    public Response post(final String context, Header authHeader, final String requestBody) {
        return given()
            .header(authHeader.getName(), authHeader.getValue())
            .contentType(ContentType.JSON)
            .body(requestBody)
            .post(baseUrl + context);
    }

    public Response get(final String context) {
        return given()
            .contentType(ContentType.URLENC)
            .get(baseUrl + context)
            .andReturn();
    }

    public Response getUrl(final String url) {
        return given()
            .contentType(ContentType.URLENC)
            .get(url)
            .andReturn();
    }

    public Response get(final String context, final Header authHeader, ContentType contentType) {
        return given()
            .cookie("USER_CONTEXT", Constants.UserContext)
            .contentType(contentType)
            .accept(ContentType.ANY)
            .header(authHeader.getName(), authHeader.getValue())
            .get(baseUrl + context)
            .andReturn();
    }

    public Response get(final String context, final Header authHeader, final String requestBody) {
        return given()
            .cookie("USER_CONTEXT", Constants.UserContext)
            .header(authHeader.getName(), authHeader.getValue())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .get(baseUrl + context)
            .andReturn();
    }

    public Response delete(final String context, final Header header) {
        return given()
            .header(header)
            .contentType(ContentType.JSON)
            .delete(baseUrl + context)
            .andReturn();
    }
}
