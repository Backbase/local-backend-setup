const baseUrl = 'http://localhost:8280';
const identityUrl = 'http://localhost:8180';

const identityRealmPath = '/auth/realms/';
const identityAuthPath = '/protocol/openid-connect/token';

// Service Names
const accessControlServiceName = 'access-control';
const arrangementManagerServiceName = 'arrangement-manager';

// Service API Paths
const accessControlPath = '/api/' + accessControlServiceName + '/client-api';
const arrangementManagerPath = '/api/' + arrangementManagerServiceName + '/client-api';

// Identity
export function getIdentityAuthUrl(realm) {
    return `${identityUrl}${identityRealmPath}${realm}${identityAuthPath}`;
}

// Access Control
export function getUserContextServiceAgreementsUrl() {
    return `${baseUrl}${accessControlPath}/v3/accessgroups/user-context/service-agreements`;
}

export function getUserContextUrl() {
    return `${baseUrl}${accessControlPath}/v2/accessgroups/usercontext`;
}

// Arrangement Manager
export function getAggregationsPath() {
    return `${baseUrl}${arrangementManagerPath}/v2/balances/aggregations`
}

export function getProductKindsPath() {
    return `${baseUrl}${arrangementManagerPath}/v2/product-kinds`
}

export function getidentityUsersPath(realm) {
    return `${identityUrl}/auth/admin/realms/${realm}/users`;
} 
