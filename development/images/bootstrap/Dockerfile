FROM maven:3-eclipse-temurin-21 AS builder

ARG JOB_VERSION
# Unpacking the bootstrap-job source code and configuring credentials using maven image default configuration folder
RUN --mount=type=cache,target=${MAVEN_CONFIG} \
    --mount=type=secret,id=mvnrepo,dst=${MAVEN_CONFIG}/settings.xml \
    --mount=type=secret,id=mvnrepokey,dst=${MAVEN_CONFIG}/settings-security.xml \
    mvn dependency:unpack -Dartifact=com.backbase.accelerators:bootstrap-job:${JOB_VERSION}:zip:src -DoutputDirectory=/usr/src/mymaven

WORKDIR /usr/src/mymaven

# Setting consistent version
RUN --mount=type=cache,target=${MAVEN_CONFIG} \
    --mount=type=secret,id=mvnrepo,dst=${MAVEN_CONFIG}/settings.xml \
    --mount=type=secret,id=mvnrepokey,dst=${MAVEN_CONFIG}/settings-security.xml \
    mvn versions:set -DnewVersion=${JOB_VERSION}

# Building the project
RUN --mount=type=cache,target=${MAVEN_CONFIG} \
    --mount=type=secret,id=mvnrepo,dst=${MAVEN_CONFIG}/settings.xml \
    --mount=type=secret,id=mvnrepokey,dst=${MAVEN_CONFIG}/settings-security.xml \
    mvn clean install -Pbootable-jar -Dskip.unit.tests=true -Dskip.integration.tests=true

# -------------------------------------------------------------------------------------------
FROM eclipse-temurin:21

RUN mkdir /opt/app

ARG JOB_VERSION
COPY --from=builder /usr/src/mymaven/job/target/bootstrap-job-${JOB_VERSION}-exec.jar-runnable.jar /opt/app/bootstrap-job.jar

ENTRYPOINT ["java", "-jar", "/opt/app/bootstrap-job.jar"]
