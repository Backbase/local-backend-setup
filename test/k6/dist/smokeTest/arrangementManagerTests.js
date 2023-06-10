/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	// The require scope
/******/ 	var __webpack_require__ = {};
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/compat get default export */
/******/ 	(() => {
/******/ 		// getDefaultExport function for compatibility with non-harmony modules
/******/ 		__webpack_require__.n = (module) => {
/******/ 			var getter = module && module.__esModule ?
/******/ 				() => (module['default']) :
/******/ 				() => (module);
/******/ 			__webpack_require__.d(getter, { a: getter });
/******/ 			return getter;
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__);

// EXPORTS
__webpack_require__.d(__webpack_exports__, {
  "default": () => (/* binding */ arrangementManagerTests),
  options: () => (/* binding */ options)
});

;// CONCATENATED MODULE: external "k6"
const external_k6_namespaceObject = require("k6");
;// CONCATENATED MODULE: external "k6/http"
const http_namespaceObject = require("k6/http");
var http_default = /*#__PURE__*/__webpack_require__.n(http_namespaceObject);
;// CONCATENATED MODULE: ./support/config/urls.ts
const baseUrl = 'http://localhost:8280';
const identityUrl = 'http://localhost:8180';
const identityRealmPath = '/auth/realms/';
const identityAuthPath = '/protocol/openid-connect/token'; // Service Names

const accessControlServiceName = 'access-control';
const arrangementManagerServiceName = 'arrangement-manager'; // Service API Paths

const accessControlPath = '/api/' + accessControlServiceName + '/client-api';
const arrangementManagerPath = '/api/' + arrangementManagerServiceName + '/client-api'; // Identity

function getIdentityAuthUrl(realm) {
  return `${identityUrl}${identityRealmPath}${realm}${identityAuthPath}`;
} // Access Control

function getUserContextServiceAgreementsUrl() {
  return `${baseUrl}${accessControlPath}/v3/accessgroups/user-context/service-agreements`;
}
function getUserContextUrl() {
  return `${baseUrl}${accessControlPath}/v2/accessgroups/usercontext`;
} // Arrangement Manager

function getAggregationsPath() {
  return `${baseUrl}${arrangementManagerPath}/v2/balances/aggregations`;
}
function getProductKindsPath() {
  return `${baseUrl}${arrangementManagerPath}/v2/product-kinds`;
}
function urls_getidentityUsersPath(realm) {
  return `${identityUrl}/auth/admin/realms/${realm}/users`;
}
;// CONCATENATED MODULE: ./support/api/identity.ts




function getAccessToken(realm, clientId) {
  let url = getIdentityAuthUrl(realm);
  let requestBody = {
    username: 'admin',
    password: 'admin',
    grant_type: 'password',
    client_id: clientId
  };
  let response = http_default().post(url, requestBody, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
  let access_token = response.json("access_token");
  (0,external_k6_namespaceObject.check)(response, {
    'auth status is 200': () => response.status === 200
  });
  (0,external_k6_namespaceObject.check)(access_token, {
    'access_token is not empty': () => access_token !== undefined
  });
  return access_token;
}
function createUser(access_token, userName) {
  let url = getidentityUsersPath(getBackbaseRealmName());
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
  let body = response?.body.toString();

  if (body !== undefined && body !== null) {
    let users = JSON.parse(body); //ToDo get user by username, rather than getting last used by default return order in response
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
;// CONCATENATED MODULE: ./support/config/constants.ts
// Realm Names
const backbaseRealmName = 'backbase';
const masterRealmName = 'master'; // Client Ids

const adminCliClientId = 'admin-cli';
const bbToolingClient = 'bb-tooling-client';
const userContext = 'eyJraWQiOiJaNXB5dkxcL3FMYUFyR3ZiTkY3Qm11UGVQU1Q4R0I5UHBPR0RvRnBlbmIxOD0iLCJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..tMfNToj_V8l223g2qq-vAQ.m_sJ7rkBrFBn9n7FDYpS_AKgeclXISyq0uPjE1-2uIjezFW6KpXahPZzyZnZMsWdCqIC_E9J_Rnw63aAa_l05OLKoh5t8h-Ksa35iJ9tn2NG_Mjl8XHwXNPpYxAe0Rxyp7tHA64E2fICGyW2NEUsa9u_DwLarRumStiZljboI12X0xv0zqN7KVBjSBRS0JrAdJ2pYxVEB-KlXdpWuNIoWwPccY4UVhvr32PPzw8AxpDdys1LDf6fxbLy6S3fy0L4LNkvKIq5gzsWD8kvnducMLIK87u9dysl-MeFrznaiecKEQVgqLFsmwRWShujcXHy.AQhfRyuBuACzuMumtlgEMw';
function constants_getBackbaseRealmName() {
  return backbaseRealmName;
}
function getMasterRealmName() {
  return masterRealmName;
}
function getAdminCliClientId() {
  return adminCliClientId;
}
function getBbToolingClient() {
  return bbToolingClient;
}
function constants_getUserContext() {
  return userContext;
}
;// CONCATENATED MODULE: ./support/api/arrangementManager.ts




function getBalancesAggregations(access_token) {
  let url = getAggregationsPath();
  let userContext = constants_getUserContext();
  let saHeaders = {
    'Accept': '*/*',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': 'Bearer ' + access_token
  };
  let saCookies = {
    USER_CONTEXT: userContext
  };
  let response = http_default().get(url, {
    headers: saHeaders,
    cookies: saCookies
  });
  (0,external_k6_namespaceObject.check)(response, {
    'getBalancesAggregations status is 200': () => response.status === 200
  });
}
function getProductKinds(access_token) {
  let url = getProductKindsPath();
  let userContext = constants_getUserContext();
  let saHeaders = {
    'Accept': '*/*',
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': 'Bearer ' + access_token
  };
  let saCookies = {
    USER_CONTEXT: userContext
  };
  let response = http_default().get(url, {
    headers: saHeaders,
    cookies: saCookies
  });
  (0,external_k6_namespaceObject.check)(response, {
    'getProductKinds status is 200': () => response.status === 200
  });
}
;// CONCATENATED MODULE: ./smokeTest/arrangementManagerTests.ts




let options = {
  stages: [{
    duration: '10s',
    target: 10
  }, {
    duration: '10s',
    target: 100
  }, {
    duration: '10s',
    target: 10
  }]
};
/* harmony default export */ const arrangementManagerTests = (() => {
  const access_token = getAccessToken(constants_getBackbaseRealmName(), getBbToolingClient());
  getBalancesAggregations(access_token);
  getProductKinds(access_token);
  (0,external_k6_namespaceObject.sleep)(1);
});
var __webpack_export_target__ = exports;
for(var i in __webpack_exports__) __webpack_export_target__[i] = __webpack_exports__[i];
if(__webpack_exports__.__esModule) Object.defineProperty(__webpack_export_target__, "__esModule", { value: true });
/******/ })()
;
//# sourceMappingURL=arrangementManagerTests.js.map