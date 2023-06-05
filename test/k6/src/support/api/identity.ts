import http from 'k6/http';
import { check } from 'k6';
import { getIdentityAuthUrl, getidentityUsersPath } from '../config/urls';
import { getUserContext } from '../config/constants';

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
  
  let url = getidentityUsersPath('backbase');
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

export function getUsers(access_token) {
  
  let url = getidentityUsersPath('backbase');
  let userContext = getUserContext();

  let headers = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let cookies = { USER_CONTEXT: userContext };

  let response = http.get(url, { headers: headers, cookies: cookies });

  check(response, {
    'getUsers response status code is 200': () => response.status === 200
  });

  return response;
}

export function deleteUser(access_token, userName) {
  
  let userToDelete = getUserByUsername(access_token, userName);
  let url = `${getidentityUsersPath('backbase')}/${userToDelete.id}`;
  let userContext = getUserContext();

  let headers = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let cookies = { USER_CONTEXT: userContext };

  let response = http.del(url, null, { headers: headers, cookies: cookies });

  check(response, {
    'deleteUser response status code is 204': () => response.status === 204
  });
}

function getUserByUsername(access_token, userName) {
  let response = getUsers(access_token);

  let body = response?.body.toString();
  
  if (body !== undefined && body !== null) {
    
    let users = JSON.parse(body);
    //ToDo get user by username, rather than getting last used by default return order in response
    //let user = users.filter(u => u.username == userName);
    let user = users[users.length - 1];

    check(user, {
      'User Found': () => user !== undefined && user !== null
    });

    return user

  } else {
    check(null, {
        'Error extracting Users': () => false
    });
  }

  return null;
}
