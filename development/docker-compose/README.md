
# Set up Backbase local environment

This guide shows you how to create a Retail App Backend Setup with a Docker Compose file.

## Contents
- [Prerequisites](#prerequisites)
- [Set up the Backbase local environment](#set-up-the-backbase-local-environment)
  - [Initial set up](#initial-set-up)
  - [Set up the local environment](#set-up-the-local-environment)
  - [Ingest data](#ingest-data)
  - [Add services](#add-services)
- [Health check](#health-check)
- [Upgrade your environment](#upgrade-your-environment)
- [Debug custom applications](#debug-custom-applications)
  - [Run the application locally](#run-the-application-locally)
  - [Debug remotely](#debug-remotely)
- [Troubleshooting](#troubleshooting)
  - [General issues](#general-issues)
  - [Useful Information](#useful-information)
  - [Colima](#colima)

## Prerequisites

For the setup, you must have the following:

- Any Docker runtime.
- Backbase repository credentials.

## Set up the Backbase local environment

### Initial set up

1. Install Colima to run Docker and work with Docker Compose:
    ```shell
    brew install colima docker docker-compose docker-credential-helper
    colima start --cpu 8 --memory 24
    ```
    > **Note 1**: Installing Colima is only for macOS. For Windows-based systems, you can install Docker Desktop and run it to start the Docker service before going to the next step.
    
    > **Note 2**: Running all the services requires around 20-24 GB of memory. If you allocate less resources, some containers would intermittently drop.
    
    > **Note 3**: If you use `colima`, then the following Docker compose commands (`docker compose`) should be replaced with `docker-compose`.

2. Log in to the Backbase repo:
    ```shell
    docker login repo.backbase.com
    ```

### Set up the local environment

1. View a list of all the running containers, with their status and configuration:
    ```shell
    docker ps
    ```
   
2. To set the backend bundle version, replace `2023.02.5-LTS` with the value of `BB_VERSION` in the [development/docker-compose/.env](https://github.com/backbase-rnd/local-backend-setup/blob/main/development/docker-compose/.env) file.

3. From the Docker Compose directory ([development/docker-compose/]()), you need to boot up the following core containers:
   1. Start up the `foundation` containers:
      ```shell
      docker compose --profile foundation up -d
      ```
      
   2. Start up the `iam` containers:
      1. Find out your local IP address on the local network. Use `ifconfig` for `MacOS`, and not the localhost address, `127.0.0.1`, but something like `192.168.1.99`.
      
      2. Set the `LOCAL_NETWORK_IP` environment variable in the [.env]() file.
      3. Start up the `iam` containers:
          ```shell
          docker compose --profile iam up -d
          ```
      4. Afterwards, you should have 4 pre-configured IAM users:
      
         | UserName  | Password  |
         |-----------|-----------|
         | admin     | admin     |
         | manager   | manager   |
         | user      | user      |
         | designer  | designer  |

4. Then, spin the following Banking Services up:
    ```shell
    docker compose --profile products --profile transactions --profile pfm --profile payments up -d
    ```

### Ingest data

It is time to ingest some data to our local environment. Run the following command to ingest data into the services:
```shell
docker compose --profile bootstrap up
```

The data ingestion above uses `BB Fuel` under the hood and run parameters can be configured via [execute-bb-fuel.sh](./scripts/bbFuel/execute-bb-fuel.sh)

### Configure Mobile Authentication

Before continuing, you need to run the Android application at least once to retrieve the FacetID from the log.
Follow the steps in the other guide, copy the FacetID (it won't change after that) and come back here...

In order to be able to login using the mobile app, you need to create the application inside Identity and store the facetId of your app on it. This can be done following these steps:

1) Get an authentication token using this command:
```bash
curl --location --request POST 'http://localhost:7777/api/token-converter/oauth/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=bb-client' \
--data-urlencode 'client_secret=bb-secret' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=api:service'
```
Store the value of the `access_token` for the next command. The token expires after few minutes, so if you need to run the same commands again, make sure you get a new token first.

2) Create the application in the fido-service:
```
curl --location --request POST 'http://fido-service:8080/service-api/v1/applications/' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {{ACCESS_TOKEN}}' \
--data-raw '{
	"appKey": "retail",
    "appId": "http://{{YOUR_IP}}:8180/auth/realms/backbase/protocol/fido-uaf/applications/retail/facets",
    "trustedFacetIds": [
    	"android:app-key-hash:com.backbase.examplebanking",
    	"android:apk-key-hash:{{FACET_ID}}",
        "ios:bundle-id:com.backbase.start"
    ]
}'
```

Replace `{{ACCESS_TOKEN}}` with the value obtained in the first step
Replace `{{YOUR_IP}}` in the appId with your IP address (not the localhost address `127.0.0.1` but the real IP address on your network)
Replace `{{FACET_ID}}` with the facetId generated for your app e.g. `Fy0Dsi2q1B475nH3o16I6ZDXjTY`

After that you will be able to login in your mobile app with the following users: "user", "designer", "manager", "admin".

### Add services

With the setup above, the following Backbase services are available:

- Edge
- Registry
- Fido Service
- Identity Server
  * With `backbase` realm included.
- Identity Integration
- Token Converter
- Access Control
- Arrangement Manager
- Budget Planner
- Payment Order Options
- Payment Order Service
- Pocket Tailor
- Transaction Manager
- Transaction Enricher
- Transaction Category Collector
- User Manager
- User Profile Manager

To add more services in the environment, insert their configuration into the `docker-compose.yaml` file.

Before proceeding, make sure that the Docker Registry is accessible.

1. Replace `SERVICE-NAME` with the service you want to add. For more information, see [Backend artifacts](https://community.backbase.com/documentation/DBS/latest/backend_artifacts).
2. Set your Docker image configuration.
3. Set the `PORT` which the service exposes.
4. You can add the following to the service environment variables:
   - To include common configurations, such as registry and signature keys, add `*common-variables`. 
   - If a database is required for your service, add `*database-variables`.
   - If the service utilizes events, include `*message-broker-variables`.

The following is an example configuration:

```yml
  <SERVICE-NAME>:
    container_name: <SERVICE_NAME>
    image:  <DOCKER-REGISTRY>/<DOCKER-REPOSITORY>:<TAG>
    ports:
      - "<PORT>:8080"
    environment:
      <<: *common-variables
      <<: *message-broker-variables
      <<: *database-variables
    volumes:
      - ./scripts:/tmp/h
    healthcheck:
      <<: *healthcheck-defaults
      test: [ "CMD", "java", "/tmp/h/HealthCheck.java", "http://registry:8080/eureka/apps/<SERVICE-NAME>", "<status>UP</status>" ]
    links:
      - registry
```

## Health check
In addition to the default health check that is provided when you use `docker compose up`, the following steps describe how to perform a more comprehensive health check on your environment using Postman:

1. Import the Postman collection from the `./test` directory.
2. Run the <b>Health Check</b> folder.
3. When all the tests pass they will change to green. This indicates that the environment is up and healthy.

    >    **NOTE**: It may take several minutes for all the services to start running. You may need to rerun the test folder multiple times until all the tests pass. 
   > 
   > If you have `jq` installed, you can display a neatly formatted output of all the services and their current health status:
    > ```shell
    > docker compose ps --format json | jq  'map({Service: .Name, Status: .Health})'
    > ```


## Upgrade your environment

To upgrade a service in the environment, change the Docker image tag to the new version. Verify that this version is compatible with the other services in the environment. 

To upgrade all services to a specific Backbase BOM version, change the `BB_VERSION` value in the [development/docker-compose/.env](https://github.com/backbase-rnd/local-backend-setup/blob/main/development/docker-compose/.env) file.

## Debug custom applications

You can debug your custom application in the local environment by either running it locally and using the environment, or by running it in the environment and using remote debugging.

### Run the application locally

To connect your application to the local environment, you can run it in the IDE and configure it to use services such as MySQL, ActiveMQ, Token Converter, and Registry. Do this by adding JVM options to the run configuration, or by editing the `application.yaml` file.

The following is an example configuration:
```
-Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
-Dbackbase.communication.http.discoverable-access-token-service=false
-Dbackbase.communication.http.access-token-uri=http://localhost:8080/api/token-converter/oauth/token
-Dspring.activemq.broker-url=tcp://localhost:61616
```
To start an application in debug mode using, for example, IntelliJ IDE, do the following:

1. Add a new Maven run configuration in IntelliJ.
2. Ensure that the working directory points to the relevant project.
3. Use the Maven command:
    ```
    spring-boot:run -Dspring-boot.run.fork=false -f pom.xml
    ```
4. Set the VM options for the application to use the local setup.
5. Run the created configuration in debug mode.

    ![ide1](docs/ide1.png)


### Debug remotely

To debug your Docker image remotely inside the local environment, do the following in your IDE:

1. Generate the Docker image locally to build a Service SDK-based custom application:
    ```
    mvn clean package -Pdocker-image,local-client -Ddocker.repo.url=local
    ```
2. Add the custom service to the `docker-compose.yaml` file and set the debug agent in the application configuration. The following is an example configuration:
    ```yml
      example-service:
        container_name: example_service
        image:  local/development/example-integration-openapi-service:latest
        ports:
          - "8090:8080"
        environment:
          <<: *common-variables
          eureka.client.enabled: 'true'
          JAVA_TOOL_OPTIONS: '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'
        volumes:
          - ./scripts:/tmp/h
        healthcheck:
          <<: *healthcheck-defaults
          test: [ "CMD", "java", "/tmp/h/HealthCheck.java", "http://registry:8080/eureka/apps/example-service", "<status>UP</status>" ]
        links:
          - registry
    ```
   To enable a Java agent on port 5005, add it to `JAVA_TOOL_OPTIONS`. If you are debugging multiple applications at the same time, use different debug ports for each application.

3. Create a Remote JVM Debug run configuration in your IDE and specify the port and arguments for each service added. The following example is for the IntelliJ IDE:

    ![ide2](docs/ide2.png)

4. Select the created configuration and run it in debug mode. When a breakpoint is reached during the run, the IDE switches to the debugger view and switches between tabs if multiple debug configurations for different applications are started.


## Troubleshooting

If the environment is not working, or if some or all of its services are not in a healthy state, do any of the following to troubleshoot the issue:

### General issues

- Check that the Docker daemon is running in the background:
  ```shell
  docker version
  ```
- Check the MySQL instance by using the Telnet command to verify that port 3306 is open and listening:
  ```shell
  telnet localhost 3306
  ```
- Check the Registry service in the browser [http://localhost:8761](http://localhost:8761).
- Check the Edge routes [http://localhost:7777/actuator/gateway/routes](http://localhost:7777/actuator/gateway/routes).
- If the health check task fails and you are operating in a new environment, ensure that you include `--profile bootstrap` in your command.

- Intermittently, some containers might drop and they should be spawned again. To do that, you can use the following command:
  ```shell
  docker compose --profile \* up <service-name> -d
  ```

### Useful information

1. To display the log output for all services specified in the `docker-compose.yaml` file and continuously update the console with new log entries:
    ```shell
    docker compose logs -f
    ```
   
2. To access your environment, use the following endpoints:
    - **Identity**: http://localhost:8180/auth
      * **Realm Admin Credentials**: `admin` / `admin`
    - **Edge Gateway**: http://localhost:8280/api
    - **Registry**: http://localhost:8761

3. The `health` indication on the containers could very well be false-negative. Please check them via the [Eureka Service Registry](http://localhost:8761/) if they are up or not.
    - For a more detailed check of your environment, use the Postman collection from the `./test` directory. For more information, see [Health check](#health-check).    

4. If you want to stop or kill containers, use one of the following:
    - Stop and remove containers in the Docker Compose file:
        ```shell
        docker compose down
        ```
    - Stop and only some containers, provide comma-separated list of containers
        ```shell
        docker compose down remote-config,transaction-enricher
        ```
    - Kill all running containers in the host:
        ```shell
        docker kill $(docker ps -q)
        ```
    - Display resource utilization per each container:
        ```shell
        docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.CPUPerc}}" | sort -k 2 -hr
        ```

5. At `2023.03` and onwards, `backbase-identity` service uses a different image and requires a non-trivial set of changes in the application configuration. If you plan to change the version, you can change the image name from the [environment variables](.env).

6. If it is required to start-over, the databases also could be needed to be cleaned up sometimes. In order to do that, run the following command to delete **ALL** the databases in the MySQL server. **Please use this with caution.**
    ```shell
    docker compose --profile clean-up-db up
    ```
   
7. If any of the service is not able to communicate with another one for some reason, and if the configuration are proper, then it is good to inspect the Docker network, if both the services are connected to the network. Make sure that both of the services are listed in the resulting response.
    ```shell
    docker network inspect backbase_default
    ```

### Colima
- If you encounter an error when running `docker compose up` in Colima, this may be caused by a problem with mounts in Docker. 
  - Symptoms include failed health checks for `Identity`, failed API calls for authentication. However, you should be able to log in using the Admin Console UI. 
  - The error message indicates that the collection could not be loaded and that there was an illegal operation on a directory. For example:
    ```
    postman_checks  | error: collection could not be loaded
    postman_checks  |   unable to read data from file "/etc/newman/Local-Backend-Environment.postman_collection.json"
    postman_checks  |   EISDIR: illegal operation on a directory, read
    ```
    
- A workaround is to use `colima delete`, but use caution as this deletes everything and restarts Colima. 
- You can also apply mount settings to your `colima` configuration and start Colima again with options for mounts.
  ```shell
  # If you don’t have any important settings to lose, you can use this option as a last resort.
  # Step 1. Run:
  colima delete
  # Step 2. Start Colima again with your usual options, but add an option for mounts.
  # Run (for example):
  colima start --cpu 8 --memory 16 --with-kubernetes --mount-type 9p
  ```


