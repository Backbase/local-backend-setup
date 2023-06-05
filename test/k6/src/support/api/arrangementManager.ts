import http from 'k6/http';
import { check } from 'k6';
import { getAggregationsPath, getProductKindsPath } from '../config/urls';
import { getUserContext } from '../config/constants';

export function getBalancesAggregations(access_token) {

  let url = getAggregationsPath();
  let userContext = getUserContext();

  let saHeaders = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let saCookies = { USER_CONTEXT: userContext };

  let response = http.get(url, { headers: saHeaders, cookies: saCookies });

  check(response, {
    'getBalancesAggregations status is 200': () => response.status === 200
  });
}

export function getProductKinds(access_token) {

  let url = getProductKindsPath();
  let userContext = getUserContext();

  let saHeaders = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let saCookies = { USER_CONTEXT: userContext };

  let response = http.get(url, { headers: saHeaders, cookies: saCookies });

  check(response, {
    'getProductKinds status is 200': () => response.status === 200
  });
}
