# Local backend setup - testing

## Prerequisites:
**NB:** Before running any tests, please ensure you have followed the guide for `docker compose`, especially for running the `bootstrap` task:
[docker-compose/readme.md](https://github.com/Backbase/local-backend-setup/blob/main/development/docker-compose/readme.md)

## RESTAssured

This contains the same Health Check and Smoke Test suites as you will find in the Postman tests, if this is your preference.
Refer to the [Postman README.md](https://github.com/Backbase/local-backend-setup/blob/main/test/postman/README.md) for more details.

The RESTAssured tests also come in two flavours, running the tests directly via the test runner, or as [Feature files via cucumber](https://cucumber.io/docs/gherkin/reference/)

The tests executed via the test runner can be found in the [main test folder](https://github.com/Backbase/local-backend-setup/tree/main/test/RESTAssured/src/test/java/com/backbase)
These tests can also be run via the command:
```
mvn clean test
```


The Feature files can currently be run from [Features test folder](https://github.com/Backbase/local-backend-setup/tree/main/test/RESTAssured/src/test/java/com/backbase/cucmber/features)
The actual Feature files are in [Features resources folder](https://github.com/Backbase/local-backend-setup/tree/main/test/RESTAssured/src/test/src/test/resources/features)

**Todo**: Configure running the tests from the Feature files