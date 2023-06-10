import http from 'k6/http';
import { check } from 'k6';
import { getUserContextServiceAgreementsUrl, getUserContextUrl } from '../config/urls';
import { getUserContext } from '../config/constants';

export function getUserContextServiceAgreements(access_token) {
  let url = getUserContextServiceAgreementsUrl();
  let userContext = getUserContext();

  let headers = { 'Accept': '*/*', 'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': 'Bearer ' + access_token };
  let cookies = { USER_CONTEXT: userContext };

  let response = http.get(url, { headers: headers, cookies: cookies });

  check(response, {
      'getUserContextServiceAgreements status is 200': () => response.status === 200
  });

  return response;
}

export function setUserContext(access_token, serviceAgreementsResponse) {
    let msa_id = extractMSAId(serviceAgreementsResponse);
    
    let url = getUserContextUrl();
    let userContext = getUserContext();
  
    let saHeaders = { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + access_token };
    let requestBody = { 'serviceAgreementId': `${msa_id}` };
    let saCookies = { USER_CONTEXT: userContext };

    let response = http.post(url, JSON.stringify(requestBody), { headers: saHeaders, cookies: saCookies });
  
    check(response, {
        'setUserContext status is 204': () => response.status === 204
    });
}

function extractMSAId(serviceAgreementsResponse) {

  let body = serviceAgreementsResponse.body;
  if (body !== undefined && body !== null) {
    var jsonData = JSON.parse(body);
    let msa_id = jsonData[0].id;
    return msa_id;
  } else {
    check(null, {
        'Error extracting MSA ID': () => false
    });
  }
}
