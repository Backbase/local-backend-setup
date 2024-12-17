ARG IDENTITY_BUILDER

FROM $IDENTITY_BUILDER AS auth-server-builder

# In this first build-stage we are configuring build-time settings and including project-specific artifacts
# (such as custom themes and authenticators) that are all input to the Quarkus build optimisation phase.

# If you are looking to configure Backbase, Keycloak (`KC_`) and Quarkus (`QUARKUS_`) settings, check if they
# are build-time or runtime settings in their respective configuration references.  Include build-time settings here.
# References:
#  - https://backbase.io
#  - https://www.keycloak.org/server/all-config
#  - https://quarkus.io/guides/all-config

# The database vendor. Supported values dev-file (default for dev purposes), mssql, mysql, oracle
ARG KC_DB="mysql"
ENV KC_DB=$KC_DB

# If set to true (default), Keycloak uses a XA transactions.
# If set to false, Keycloak uses a non-XA datasource in case the database does not support XA transactions.
# If KC_DB=mssql, then you must to disable XA Transactions
ARG KC_TRANSACTION_XA_ENABLED="false"
ENV KC_TRANSACTION_XA_ENABLED=$KC_TRANSACTION_XA_ENABLED

# Expose health check endpoints. Must be set to true if using k8s probes
ENV KC_HEALTH_ENABLED "true"
# If enabled, metrics are available at the '/metrics' endpoint.
ENV KC_METRICS_ENABLED "false"

# If building an image specifically to run the Liquibase migration and exit (e.g. a Job/initContainer)
# then override the default cache config to prevent it from joining the Infinispan cluster...
#     ENV KC_CACHE_CONFIG_FILE "cache-local.xml"
#
# ...and when running the container, specify the following environment variable at runtime
#     KC_EXIT_AFTER_DB_MIGRATION: "true"

# Include custom realm configuration JSON files in `${kc.home.dir}/data/import/`.  These files will be imported once on first run of
# Auth Server.
COPY realms/* /opt/keycloak/data/import/

ENV JDK_JAVA_OPTIONS "-XX:+UseG1GC -XX:+AlwaysPreTouch -Dkc.home.dir=/opt/keycloak -Djboss.server.config.dir=/opt/keycloak/conf"
RUN ["java", "-cp", "/opt/keycloak/lib/quarkus-run.jar:/opt/keycloak/lib/bootstrap/*",\
    "-Djava.net.preferIPv4Stack=true", "-Dfile.encoding=UTF-8",\
    "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", "-Dquarkus-log-max-startup-records=10000",\
    "io.quarkus.bootstrap.runner.QuarkusEntryPoint", "build"]

# -------------------------------------------------------------------------------------------
# The production image build; for example, is based on the `distroless` Java image
FROM gcr.io/distroless/java21-debian12

# Copy the files prepared in the previous build-stage
COPY --from=auth-server-builder /opt/keycloak /opt/keycloak

ENV JDK_JAVA_OPTIONS "-XX:+UseG1GC -XX:+AlwaysPreTouch -Dkc.home.dir=/opt/keycloak -Djboss.server.config.dir=/opt/keycloak/conf"
ENTRYPOINT ["java", "-cp", "/opt/keycloak/lib/quarkus-run.jar:/opt/keycloak/lib/bootstrap/*",\
    "-Djava.net.preferIPv4Stack=true", "-Dfile.encoding=UTF-8",\
    "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", "-Dquarkus-log-max-startup-records=10000",\
    "io.quarkus.bootstrap.runner.QuarkusEntryPoint"]

CMD ["start-dev", "--import-realm"]
