#!/bin/sh
if [ $# -ne 2 ]; then
  echo "Usage: $0 <repo_username> <repo_api_key>"
  exit 1
fi
source ../docker-compose/.env
export ARTIFACTS_CREDS_USR=$1
export ARTIFACTS_CREDS_PSW=$2

mvn clean process-resources -Dbootstrap.job.version="$BOOTSTRAP_JOB_VERSION" -s settings.xml

cd target/bootstrap-job

mvn clean install -s ../../settings.xml

mvn -ntp -B package -s ../../settings.xml -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag \
          -Dskip.unit.tests=true -Dskip.integration.tests=true  -Dimage=bootstrap-job \
          -Djib.to.tags="$BOOTSTRAP_JOB_VERSION"
