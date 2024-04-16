# Local backend setup

This repository has the required files for backend developers to create a basic local environment. Instructions on how to set up this environment are found under [development](development).

## Quick start

Set up a Docker local environment on your laptop by running these commands in the terminal:

1. Clone the repository:
    ```
    git clone git@github.com:backbase/local-backend-setup.git
    ```
2. Switch to the Docker Compose directory:
    ```
    cd local-backend-setup/development/docker-compose
    ```
3. Start the health checks and ensure that all services are running: 
    ```
    docker compose --profile=bootstrap up
    ```
   In case the bootstrap fails due to some error, please restart the service 2-3 times using above command, it will run successfully, i will fix that issue later.

4. Start the retail onboarding services
   ```
   docker-compose --profile=retail-onboarding up -d
   ```
   
   If retail-onboarding service fails due to liquibase changes issue, connect to mysql and run following
   ```sql
   ALTER TABLE flw_case_milestone
   MODIFY COLUMN case_key varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL;
   commit;
   ```
   It will again fail on the new run but with different error, now run the following
   ```sql
   ALTER TABLE flw_case_epic
   MODIFY COLUMN case_key varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL;
   commit;
   ```

   Once all the required services have started, you can view the health check results in your terminal. To check the status of the services, open the [Registry](http://localhost:8761) in your web browser. Additionally, you can import the [Postman collection](test/postman/HealthCheck_Local-Backend-Environment.postman_collection.json) to perform a more comprehensive health check on your environment using Postman.  
   &nbsp;  
   For more information, see [Set up Backbase local environment](https://github.com/backbase/local-backend-setup/tree/main/development/docker-compose#set-up-backbase-local-environment).


## Create a new service

**NB:** EMU account required to access (https://github.com/baas-devops-reference)

To create custom services in the Backbase ecosystem, Backbase recommends using [ModelBank templates](https://github.com/baas-devops-reference?q=template&type=all&sort=). If you don't have an EMU account, you can find detailed instructions on creating a service using the service SDK (core-service-archetype) in the [create a service guidelines](https://backbase.io/developers/documentation/backend-devkit/latest/development/create-a-service/).

Use these guidelines to develop your own service, and use the Backbase local environment to test and run your code. 

For more information, see [ModelBank organization](https://github.com/baas-devops-reference).

## Release Policy

We release this repository after every Backbase Breaking Changes release. Each release is compatible with all the Backbase versions until its next LTS version.
For example, with the `2022.03` release, you can use any Backbase version <ins>Higher</ins> than `2022.03` up to `2023.09-LTS`.
