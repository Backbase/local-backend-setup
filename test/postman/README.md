# Postman Collections

## Prerequisites:
**NB:** Before running any tests, please ensure you have followed the guide for `docker compose`, especially for running the `bootstrap` task:
[docker-compose/readme.md](https://github.com/Backbase/local-backend-setup/blob/main/development/docker-compose/readme.md)

## Postman Collections and Environment Files
Two collections are provided for the current setup:
- `HealthCheck_Local-Backend-Environment.postman_collection.json` for providing HealthChecks for services
- `SmokeTest_Local-Backend-Environment.postman_collection.json` for additional basic tests of some services

Two environment files are also provided for the current setup:
- `Local-Backend-Environment.postman_environment.json` for local use with Postman
- `Docker-Backend-Environment.postman_environment.json` applied with `docker compose up` to run HealthChecks

## Tests run on `docker compose up`
HealthCheck Tests run on `docker compose up` are defined in the docker compose file:
- [Postman Health Check](https://github.com/search?q=repo%3ABackbase%2Flocal-backend-setup%20postman-health-check%3A&type=code)

These can be amended or removed using the above file entry, to change or remove what is run on `docker compose up`

## RESTAssured
These tests have also been duplicated in RESTAssured, if this is your preference.
Refer to the [RESTAssured README.md](https://github.com/Backbase/local-backend-setup/blob/main/test/RESTAssured/readme.md) for more details.