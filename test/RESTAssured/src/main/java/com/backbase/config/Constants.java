package com.backbase.config;

public class Constants {

    // Health Checks
    public static final String baseUrl = "http://localhost:8280";
    public static final String identityUrl = "http://localhost:8180";
    public static final String registryUrl = "http://localhost:8761";

    public static final String edgeActuatorHealthPath = "/actuator/health";
    public static final String upHealthCheckResponseBody = "{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}";

    public static final String registryAppPath = "/eureka/apps/";
    public static final String upRegistryAppResponseBody = "<status>UP</status>";

    // Realm Names
    public static final String backbaseRealmName = "backbase";
    public static final String masterRealmName = "master";

    // Client Ids
    public static final String adminCliClientId = "admin-cli";
    public static final String bbToolingClient = "bb-tooling-client";

    public static final String identityWellKnowConfigPath = "/auth/realms/" + backbaseRealmName + "/.well-known/openid-configuration";
    public static final String identityRealmPath = "/auth/realms/";
    public static final String identityAuthPath = "/protocol/openid-connect/token";
    public static final String identityUsersPath = "/auth/admin/realms/" + backbaseRealmName + "/users";

    // Service Names
    public static final String accessControlServiceName = "access-control";
    public static final String arrangementManagerServiceName = "arrangement-manager";
    public static final String tokenConverterServiceName = "token-converter";
    public static final String userManagerServiceName = "user-manager";

    // Service API Paths
    public static final String accessControlPath = "/api/" + accessControlServiceName + "/client-api";
    public static final String arrangementManagerPath = "/api/" + arrangementManagerServiceName + "/client-api";


    public static final String UserContext =
        "eyJraWQiOiJaNXB5dkxcL3FMYUFyR3ZiTkY3Qm11UGVQU1Q4R0I5UHBPR0RvRnBlbmIxOD0iLCJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..P1mCuwcmRIvHxm_m_vAUbQ.zxayuADWboDYK-2G0kfeODy1P_JqBR8vXPNh7foeqIWSQQ3mHv5pfuffbQsR2RyeqGXg5G7-SSe1C36s89apAg8tGUKd10dfkwYVCEdn4sT7NAfs_5c3snOX3GCX6fxklx7EK82-5eXCyFKOYm2Jde1-bBgIWWy-ijnQpxl1UQSly33J_u2x8PCSfcNqnxVXi7c08UNEjtNlP3jzkVjQfk0tigrJj95wZIaLD2V48vH2B7nui3mYrEHJGLop9BAfDAIo6bOgSTCz3gdLDcrK8hmE7UEWs170NA6JoABR1GA0_5UuQdhb5R7zo4_zSbDR.EZW49TkRtrHUmb2jZo68ZQ";
}

