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

const userContext = 
  'eyJraWQiOiJaNXB5dkxcL3FMYUFyR3ZiTkY3Qm11UGVQU1Q4R0I5UHBPR0RvRnBlbmIxOD0iLCJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..tMfNToj_V8l223g2qq-vAQ.m_sJ7rkBrFBn9n7FDYpS_AKgeclXISyq0uPjE1-2uIjezFW6KpXahPZzyZnZMsWdCqIC_E9J_Rnw63aAa_l05OLKoh5t8h-Ksa35iJ9tn2NG_Mjl8XHwXNPpYxAe0Rxyp7tHA64E2fICGyW2NEUsa9u_DwLarRumStiZljboI12X0xv0zqN7KVBjSBRS0JrAdJ2pYxVEB-KlXdpWuNIoWwPccY4UVhvr32PPzw8AxpDdys1LDf6fxbLy6S3fy0L4LNkvKIq5gzsWD8kvnducMLIK87u9dysl-MeFrznaiecKEQVgqLFsmwRWShujcXHy.AQhfRyuBuACzuMumtlgEMw';

export let options = {
  stages: [
    { duration: '1s', target: 1 }
  // ToDo: Requires getting user id by username to run multiple VUs
  //  { duration: '10s', target: 10 },
  //  { duration: '10s', target: 100 },
  //  { duration: '10s', target: 10 },
]};

export default function() {
  const access_token = getAccessToken('master', 'admin-cli');
  const currentDate = new Date();
  const timestamp = currentDate.getTime();
  const userName = 'ZZ_k6User_' + timestamp + Math.random();
  createUser(access_token, userName);
  getUsers(access_token);
  deleteUser(access_token, userName);
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

function getIdentityAuthUrl(realm) {
  return `${identityUrl}${identityRealmPath}${realm}${identityAuthPath}`;
}

function getidentityUsersPath(realm) {
  return `${identityUrl}/auth/admin/realms/${realm}/users`;
}

function getUserContext() {
  return userContext;
}

function createUser(access_token, userName) {
  let url = getidentityUsersPath('backbase');
  let headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + access_token
  };
  let userContext = getUserContext();
  let cookies = {
    USER_CONTEXT: userContext
  };
  let requestBody = {
    'username': `${userName}`,
    'enabled': 'true',
    'firstName': 'BE',
    'lastName': 'Dev',
    'credentials': [{
      'type': 'password',
      'value': 'password'
    }]
  };
  let response = http.post(url, JSON.stringify(requestBody), {
    headers: headers,
    cookies: cookies
  });
  check(response, {
    'createUser response status code is 201': () => response.status === 201
  });
}

function getUsers(access_token) {
  let url = getidentityUsersPath('backbase');
  let userContext = getUserContext();
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
    'getUsers response status code is 200': () => response.status === 200
  });
  return response;
}

function deleteUser(access_token, userName) {
  let userToDelete = getUserByUsername(access_token, userName);
  let url = `${getidentityUsersPath('backbase')}/${userToDelete.id}`;
  let userContext = getUserContext();
  let headers = {
    'Accept': '*/*',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': 'Bearer ' + access_token
  };
  let cookies = {
    USER_CONTEXT: userContext
  };
  let response = http.del(url, null, {
    headers: headers,
    cookies: cookies
  });
  check(response, {
    'deleteUser response status code is 204': () => response.status === 204
  });
}

function getUserByUsername(access_token, userName) {
  let response = getUsers(access_token);
  let body = response.body.toString();

  if (body !== undefined && body !== null) {
    let users = JSON.parse(body); 
    //ToDo get user by username, rather than getting last used by default return order in response
    //let user = users.filter(u => u.username == userName);

    let user = users[users.length - 1];
    check(user, {
      'User Found': () => user !== undefined && user !== null
    });
    return user;
  } else {
    check(null, {
      'Error extracting Users': () => false
    });
  }

  return null;
}
