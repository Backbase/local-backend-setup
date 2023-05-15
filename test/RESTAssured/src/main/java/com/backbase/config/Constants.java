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
        "eyJraWQiOiJaNXB5dkxcL3FMYUFyR3ZiTkY3Qm11UGVQU1Q4R0I5UHBPR0RvRnBlbmIxOD0iLCJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..tMfNToj_V8l223g2qq-vAQ.m_sJ7rkBrFBn9n7FDYpS_AKgeclXISyq0uPjE1-2uIjezFW6KpXahPZzyZnZMsWdCqIC_E9J_Rnw63aAa_l05OLKoh5t8h-Ksa35iJ9tn2NG_Mjl8XHwXNPpYxAe0Rxyp7tHA64E2fICGyW2NEUsa9u_DwLarRumStiZljboI12X0xv0zqN7KVBjSBRS0JrAdJ2pYxVEB-KlXdpWuNIoWwPccY4UVhvr32PPzw8AxpDdys1LDf6fxbLy6S3fy0L4LNkvKIq5gzsWD8kvnducMLIK87u9dysl-MeFrznaiecKEQVgqLFsmwRWShujcXHy.AQhfRyuBuACzuMumtlgEMw";
}

