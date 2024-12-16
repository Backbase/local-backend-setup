FROM maven:3-eclipse-temurin-21 AS builder

ARG JOB_VERSION
RUN --mount=type=secret,id=mvnrepo,dst=./settings.xml mvn -s settings.xml dependency:unpack -Dartifact=com.backbase.accelerators:bootstrap-job:${JOB_VERSION}:zip:src -DoutputDirectory=/usr/src/mymaven

WORKDIR /usr/src/mymaven

RUN --mount=type=secret,id=mvnrepo,dst=./settings.xml mvn -s settings.xml versions:set -DnewVersion=${JOB_VERSION}
RUN --mount=type=secret,id=mvnrepo,dst=./settings.xml mvn -s settings.xml clean install -Pbootable-jar -Dskip.unit.tests=true -Dskip.integration.tests=true

# -------------------------------------------------------------------------------------------
FROM eclipse-temurin:21

RUN mkdir /opt/app

ARG JOB_VERSION
COPY --from=builder /usr/src/mymaven/job/target/bootstrap-job-${JOB_VERSION}-exec.jar-runnable.jar /opt/app/bootstrap-job.jar

ENTRYPOINT ["java", "-jar", "/opt/app/bootstrap-job.jar"]
