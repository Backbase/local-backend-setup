import { sleep } from 'k6';
import { getAccessToken } from '../support/api/identity';
import { getBalancesAggregations, getProductKinds } from '../support/api/arrangementManager';

export let options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 100 },
    { duration: '10s', target: 10 },
  ],
};

export default () => {
  const access_token = getAccessToken('backbase', 'bb-tooling-client');
  
  getBalancesAggregations(access_token);
  getProductKinds(access_token);
  
  sleep(1);
};
