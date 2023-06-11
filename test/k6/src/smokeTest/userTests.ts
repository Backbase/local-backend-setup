import { sleep } from 'k6';
import { createUser, deleteUser, getAccessToken, getUserIdByUsername, getUsers } from '../support/api/identity';
import { getAdminCliClientId, getMasterRealmName, getUserNamePrefix } from '../support/config/constants';

export let options = {
    stages: [
      { duration: '10s', target: 10 },
      { duration: '10s', target: 100 },
      { duration: '10s', target: 10 },
    ],
  };
  
  export default () => {
    let access_token = getAccessToken(getMasterRealmName(), getAdminCliClientId());
    let currentDate = new Date();
    let timestamp = currentDate.getTime();

    let userName = getUserNamePrefix() + timestamp + Math.random();
    
    createUser(access_token, userName);

    let users = getUsers(access_token, userName);
    let userId = getUserIdByUsername(users, userName);
    
    deleteUser(access_token, userId);
    
    sleep(1);
  };
