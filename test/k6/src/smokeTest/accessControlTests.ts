import { sleep } from 'k6';
import { getAccessToken } from '../support/api/identity';
import { getUserContextServiceAgreements, setUserContext } from '../support/api/accessControl';
import { getBackbaseRealmName, getBbToolingClient } from '../support/config/constants';

export let options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 100 },
    { duration: '10s', target: 10 },
  ],
};

export default () => {
  const access_token = getAccessToken(getBackbaseRealmName(), getBbToolingClient());

  let serviceAgreementsResponse = getUserContextServiceAgreements(access_token);
  setUserContext(access_token, serviceAgreementsResponse);
  
  sleep(1);
};
