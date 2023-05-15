package com.backbase.services.services;

import static java.util.stream.Collectors.toList;

import com.backbase.config.Constants;
import com.backbase.domain.User;
import com.backbase.util.ApplicationHttpUtils;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.net.HttpCookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

public class Identity extends BaseService {

    public String token;
    private final String username = "backender";

    public Identity() {
        super("", Constants.identityUrl);
    }

    public void aUserHasAuthenticated(final String realm, final String client_id) {
        response = applicationHttpUtils.identityLogin(realm, client_id);
        Assert.assertEquals(200, response.getStatusCode());
        storeAccessToken();
    }

    public void anIdentityConfigCheckIsRequested() {
        response = applicationHttpUtils.get(Constants.identityWellKnowConfigPath);
    }

    public void theUserMakesACreateUserRequest() {
        UserHasTokensStored();
        String usersPath = Constants.identityUsersPath;
        Header authHeader = new Header("Authorization", "Bearer " + token);
        String createUserRequestBody =
            "{\n" +
                "    \"username\": \"" + username + "\",\n" +
                "    \"enabled\": \"true\",\n" +
                "    \"firstName\": \"BE\",\n" +
                "    \"lastName\": \"Dev\",\n" +
                "    \"credentials\": [\n" +
                "        {\n" +
                "            \"type\": \"password\",\n" +
                "            \"value\": \"password\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        response = applicationHttpUtils.post(usersPath, authHeader, createUserRequestBody);

        Assert.assertEquals(201, response.getStatusCode());
    }

    public void theUserMakesAGetUsersRequest() {
        UserHasTokensStored();
        String usersPath = Constants.identityUsersPath;
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(usersPath, authHeader, ContentType.URLENC);
        Assert.assertEquals(200, response.getStatusCode());
    }

    public void theUserMakesADeleteUserRequest() {
        UserHasTokensStored();
        User userToDelete = getUserByUsername(username);
        Assert.assertNotNull(userToDelete);
        String usersPath = Constants.identityUsersPath + "/" + userToDelete.id;
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.delete(usersPath, authHeader);
        Assert.assertEquals(204, response.getStatusCode());
    }

    public void theIdentityConfigCheckReturnsBackbase() {
        Assert.assertNotNull(response);
        String responseBody = ApplicationHttpUtils.getResponseBody(response);
        Assert.assertEquals(200, response.statusCode());
        Assert.assertTrue(responseBody.contains("backbase"));
    }

    public void UserHasTokensStored() {
        Assert.assertNotNull(token);
    }

    private String getPropertyFromResponseBody(String propertyName) {
        Gson gson = new Gson();
        Map<String, String> responseMap = gson.<Map<String, String>>
            fromJson(ApplicationHttpUtils.getResponseBody(response), HashMap.class);
        return responseMap != null ? responseMap.get(propertyName) : null;
    }

    private String getAccessTokenFromResponseBody() {
        return getPropertyFromResponseBody("access_token");
    }

    private void storeAccessToken() {
        List<HttpCookie> authorizationCookies = response.getHeaders().getList("Set-Cookie").stream()
            .filter(header -> header.getValue().contains("Authorization"))
            .flatMap(header -> HttpCookie.parse(header.getValue()).stream())
            .collect(toList());
        Assert.assertFalse(authorizationCookies.size() > 1);
        token = authorizationCookies.stream()
            .findFirst()
            .map(HttpCookie::getValue)
            .orElseGet(this::getAccessTokenFromResponseBody);
    }

    private User getUserByUsername(String username) {
        String usersPath = Constants.identityUsersPath;
        Header authHeader = new Header("Authorization", "Bearer " + token);
        response = applicationHttpUtils.get(usersPath, authHeader, ContentType.URLENC);

        List<User> users = Arrays.asList(response.getBody().as(User[].class));

        for (User user : users)
            if (user.username.equals(username)) {
                return user;
            }

        return null;
    }
}
