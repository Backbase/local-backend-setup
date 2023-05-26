# Local backend setup - testing

## Prerequisites:
**NB:** Before running any tests, please ensure you have followed the guide for `docker compose`, especially for running the `bootstrap` task:
[docker-compose/readme.md](https://github.com/Backbase/local-backend-setup/blob/main/development/docker-compose/readme.md)

The tests come in two flavours:
```
├── test
│   ├── postman
│   └── RESTAssured     
```

## Postman

Refer to the [Postman README.md](https://github.com/Backbase/local-backend-setup/blob/main/test/postman/README.md) for more details.

### Note: Tests run on `docker compose up`
HealthCheck Tests run on `docker compose up` are defined in the docker compose file:
- [Postman Health Check](https://github.com/search?q=repo%3ABackbase%2Flocal-backend-setup%20postman-health-check%3A&type=code)

These can be amended or removed using the above file entry, to change or remove what is run on `docker compose up`

## RESTAssured

Refer to the [RESTAssured README.md](https://github.com/Backbase/local-backend-setup/blob/main/test/RESTAssured/README.md) for more details.

This contains the same Health Check and Smoke Test suites as you will find in the Postman tests.
However, these also come in two flavours, running the tests directly via the test runner, or as [Feature files via cucumber](https://cucumber.io/docs/gherkin/reference/)