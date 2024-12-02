#!/bin/sh
if [ $# -ne 3 ]; then
  echo "Usage: $0 <bootstrap_job_version> <repo_username> <repo_api_key>"
  exit 1
fi

BOOTSTRAP_JOB_VERSION=$1
export ARTIFACTS_CREDS_USR=$2
export ARTIFACTS_CREDS_PSW=$3

mvn clean process-resources -Dbootstrap.job.version="${BOOTSTRAP_JOB_VERSION}" -s settings.xml

cd target/bootstrap-job

mvn clean install -s ../../settings.xml

docker run -d -p 8000:5000 --name local-registry registry:2

#curl -X GET http://localhost:8000/v2/_catalog

mvn -ntp -B package -s ../../settings.xml -pl :bootstrap-job -Pdocker-image -Pno-latest-tag -Dskip.unit.tests=true \
          -Dskip.integration.tests=true -DskipDocker -Dimage=127.0.0.1:8000/bootstrap-job \
          -Djib.to.tags="${BOOTSTRAP_JOB_VERSION}" -Djib.allowInsecureRegistries=true

docker pull localhost:8000/bootstrap-job:${BOOTSTRAP_JOB_VERSION}

docker stop local-registry
docker remove local-registry
