import http from 'k6/http';
import { check } from 'k6';
import { getIdentityAuthUrl, getidentityUsersPath } from '../config/urls';
import { getBackbaseRealmName, getUserContext } from '../config/constants';

export function getAccessToken(realm, clientId) {
  let url = getIdentityAuthUrl(realm);

  let requestBody = { username: 'admin', password: 'admin', grant_type: 'password', client_id: clientId };
  let response = http.post(url, requestBody, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } });
  let access_token = response.json("access_token");

  check(response, {
    'auth status is 200': () => response.status === 200
  });

  check(access_token, {
    'access_token is not empty': () => access_token !== undefined
  })
  
  return access_token;
}

export function createUser(access_token, userName) {
  
  let url = getidentityUsersPath(getBackbaseRealmName());
  let headers = { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + access_token };
  let userContext = getUserContext();
  let cookies = { USER_CONTEXT: userContext };

  let requestBody = { 
        'username': `${userName}`,
        'enabled': 'true',
        'firstName': 'BE',
        'lastName': 'Dev',
        'credentials': [
            {
                'type': 'password',
                'value': 'password'
            }
        ]  
  };
  
  let response = http.post(url, JSON.stringify(requestBody), { headers: headers, cookies: cookies });

  check(response, {
    'createUser response status code is 201': () => response.status === 201
  });
}

export function getUsers(access_token, userName) {
  
  let url = getidentityUsersPath(getBackbaseRealmName());
  let userContext = getUserContext();

  let headers = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let cookies = { USER_CONTEXT: userContext };

  let response = http.get(url, { headers: headers, cookies: cookies });

  check(response, {
    'getUsers response status code is 200': () => response.status === 200
  });

  let body = response?.body?.toString();

  if (body !== undefined && body !== null) {
    check(response, {
      'getUsers response contains userName': () => String(body).includes(userName)
    });
  } else {
    check(null, {
      'Error extracting Users': () => false
    });
  }

  return response;
}


export function getUserIdByUsername(getUsersResponse, userName) {
  
  let body = getUsersResponse.body.toString();
  
  if (body !== undefined && body !== null) {
    
    let users = JSON.parse(body);
    let userFilter = users.filter(u => u.username == userName);
    let user = JSON.parse(JSON.stringify(userFilter))[0];

    check(user, {
      'User Found': () => user !== undefined && user !== null && user.username == userName
    });

    return user.id;

  } else {
    check(null, {
        'Error extracting Users': () => false
    });
  }

  return null;
}

export function deleteUser(access_token, userId) {
  
  let url = `${getidentityUsersPath('backbase')}/${userId}`;
  let userContext = getUserContext();

  let headers = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let cookies = { USER_CONTEXT: userContext };

  let response = http.del(url, null, { headers: headers, cookies: cookies });

  check(response, {
    'deleteUser response status code is 204': () => response.status === 204
  });
}

