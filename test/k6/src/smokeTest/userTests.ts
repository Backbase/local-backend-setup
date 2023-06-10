import { sleep } from 'k6';
import { createUser, deleteUser, getAccessToken, getUsers } from '../support/api/identity';
import { getAdminCliClientId, getMasterRealmName } from '../support/config/constants';

export let options = {
    stages: [
      { duration: '1s', target: 1 },
    // ToDo: Requires getting user id by username to run multiple VUs
    //  { duration: '10s', target: 10 },
    //  { duration: '10s', target: 100 },
    //  { duration: '10s', target: 10 },
    ],
  };
  
  export default () => {
    const access_token = getAccessToken(getMasterRealmName(), getAdminCliClientId());
    const currentDate = new Date();
    const timestamp = currentDate.getTime();

    const userName = 'ZZ_k6User_' + timestamp + Math.random();

    createUser(access_token, userName);
    getUsers(access_token);
    deleteUser(access_token, userName);
    
    sleep(1);
  };