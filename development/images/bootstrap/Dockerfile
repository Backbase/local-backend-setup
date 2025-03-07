FROM maven:3-eclipse-temurin-21 AS builder

ARG JOB_VERSION
RUN --mount=type=cache,target=/root/.m2/repository \
    --mount=type=secret,id=mvnrepo,dst=/root/.m2/settings.xml \
    --mount=type=secret,id=mvnrepokey,dst=/root/.m2/settings-security.xml \
    mvn dependency:unpack -Dartifact=com.backbase.accelerators:bootstrap-job:${JOB_VERSION}:zip:src -DoutputDirectory=/usr/src/mymaven

WORKDIR /usr/src/mymaven

RUN --mount=type=cache,target=/root/.m2/repository \
    --mount=type=secret,id=mvnrepo,dst=/root/.m2/settings.xml \
    --mount=type=secret,id=mvnrepokey,dst=/root/.m2/settings-security.xml \
    mvn versions:set -DnewVersion=${JOB_VERSION} && mvn clean install -Pbootable-jar -Dskip.unit.tests=true -Dskip.integration.tests=true

# -------------------------------------------------------------------------------------------
FROM eclipse-temurin:21

RUN mkdir /opt/app

ARG JOB_VERSION
COPY --from=builder /usr/src/mymaven/job/target/bootstrap-job-${JOB_VERSION}-exec.jar-runnable.jar /opt/app/bootstrap-job.jar

ENTRYPOINT ["java", "-jar", "/opt/app/bootstrap-job.jar"]
