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

mkdir data/src/main/resources/LBS
cp ../../data/* data/src/main/resources/LBS

mvn clean install -s ../../settings.xml

mvn -ntp -B package -s ../../settings.xml -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag \
          -Dskip.unit.tests=true -Dskip.integration.tests=true  -Dimage=bootstrap-job \
          -Djib.to.tags="${BOOTSTRAP_JOB_VERSION}"
