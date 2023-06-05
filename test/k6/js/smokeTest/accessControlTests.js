/*
This test was created using the dist/smoketest test file that has been transpiled from the related TypeScript files.
It has only been included as a rough example of a JavaScript based test
*/

import http from "k6/http";
import { check, sleep } from "k6";

const baseUrl = 'http://localhost:8280';
const identityUrl = 'http://localhost:8180';
const identityRealmPath = '/auth/realms/';
const identityAuthPath = '/protocol/openid-connect/token';

const accessControlServiceName = 'access-control';
const accessControlPath = '/api/' + accessControlServiceName + '/client-api';

const userContext = 
  'eyJraWQiOiJaNXB5dkxcL3FMYUFyR3ZiTkY3Qm11UGVQU1Q4R0I5UHBPR0RvRnBlbmIxOD0iLCJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..tMfNToj_V8l223g2qq-vAQ.m_sJ7rkBrFBn9n7FDYpS_AKgeclXISyq0uPjE1-2uIjezFW6KpXahPZzyZnZMsWdCqIC_E9J_Rnw63aAa_l05OLKoh5t8h-Ksa35iJ9tn2NG_Mjl8XHwXNPpYxAe0Rxyp7tHA64E2fICGyW2NEUsa9u_DwLarRumStiZljboI12X0xv0zqN7KVBjSBRS0JrAdJ2pYxVEB-KlXdpWuNIoWwPccY4UVhvr32PPzw8AxpDdys1LDf6fxbLy6S3fy0L4LNkvKIq5gzsWD8kvnducMLIK87u9dysl-MeFrznaiecKEQVgqLFsmwRWShujcXHy.AQhfRyuBuACzuMumtlgEMw';

export let options = {
  stages: [
    { duration: '10s', target: 10 }, 
    { duration: '10s', target: 100 }, 
    { duration: '10s', target: 10  }
]};

export default function() {
  const access_token = getAccessToken('backbase', 'bb-tooling-client');
  let serviceAgreementsResponse = getUserContextServiceAgreements(access_token);

  sleep(1);
};

function getAccessToken(realm, clientId) {
  let url = getIdentityAuthUrl(realm);
  let requestBody = {
    username: 'admin',
    password: 'admin',
    grant_type: 'password',
    client_id: clientId
  };
  let response = http.post(url, requestBody, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
  let access_token = response.json("access_token");
  check(response, {
    'auth status is 200': () => response.status === 200
  });
  check(access_token, {
    'access_token is not empty': () => access_token !== undefined
  });
  return access_token;
}

function getUserContextServiceAgreements(access_token) {
  let url = getUserContextServiceAgreementsUrl();
  let userContext = constants_getUserContext();
  let headers = {
    'Accept': '*/*',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': 'Bearer ' + access_token
  };
  let cookies = {
    USER_CONTEXT: userContext
  };
  let response = http.get(url, {
    headers: headers,
    cookies: cookies
  });
  check(response, {
    'getUserContextServiceAgreements status is 200': () => response.status === 200
  });
  return response;
}

function getIdentityAuthUrl(realm) {
  return `${identityUrl}${identityRealmPath}${realm}${identityAuthPath}`;
}

function getUserContextServiceAgreementsUrl() {
  return `${baseUrl}${accessControlPath}/v3/accessgroups/user-context/service-agreements`;
}

function constants_getUserContext() {
  return userContext;
}
